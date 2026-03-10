import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import kotlin.enums.enumEntries

plugins {
    scala
    /* see "Chisel-Circulator problem" paragraph in docs/notes/architecture.md */
}

repositories {
    mavenCentral()
}

// Scala and Chisel configuration
dependencies {
    implementation("org.scala-lang:scala-library:2.13.15")
    implementation("org.chipsalliance:chisel_2.13:6.7.0")
    scalaCompilerPlugins("org.chipsalliance:chisel-plugin_2.13.15:6.7.0")
}

scala {
    scalaVersion = "2.13.15"
    zincVersion = "1.10.4"
    sourceSets {
        val main by getting {
            scala.srcDirs("src/main/chisel")
        }
    }
}

tasks.withType<ScalaCompile>().configureEach {
    scalaCompileOptions.additionalParameters = listOf(
        "-language:reflectiveCalls",
        "-deprecation",
        "-feature",
        "-Xcheckinit",
        "-Ymacro-annotations"
    )
}

val generatedDir = layout.buildDirectory.dir("generated/sources/circulator")

val chiselOutputDir = generatedDir.get().dir("chisel")
val firtoolOutputDir = generatedDir.get().dir("firtool")
val arcilatorOutputDir = generatedDir.get().dir("arcilator")
val clangOutputDir = generatedDir.get().dir("clang")

fun chisel(name: String, mainClass: String, jvmArgs: List<String> = emptyList()): TaskProvider<JavaExec> {
    val formattedName = name.split("\\s+".toRegex()).joinToString("") {
        it.lowercase().replaceFirstChar { c -> c.uppercase() }
    }

    val task = tasks.register<JavaExec>("compile${formattedName}Chisel") {
        group = "circulator"
        description = "Run Chisel FIRRTL producing for model $name"
        classpath = sourceSets["main"].runtimeClasspath

        inputs.file("src/main/chisel/io/github/e1turin/circulator/demo/counter/counter.scala")
        outputs.dir(chiselOutputDir)
        this.mainClass = mainClass
        this.jvmArgs = jvmArgs
    }

    return task
}

val modelName = "ClickCounter" //"CounterChisel"
val modelDir = "counter"

val compileChisel = chisel(
    "Counter",
    mainClass = "io.github.e1turin.circulator.demo.counter.Main",
    jvmArgs = listOf("-Dchisel.output.dir=${chiselOutputDir}/counter")
)


enum class FirtoolOutputAction(val flags: Array<String>, val fileExtension: String) {
    ParseOnly(arrayOf("--parse-only"), "fir.mlir"),
    IrFir(arrayOf("--ir-fir"), "fir.mlir"),
    IrHw(arrayOf("--ir-hw"), "hw.mlir"),
    IrSv(arrayOf("--ir-sv"), "sv.mlir"),
    IrVerilog(arrayOf("--ir-verilog"), "v.mlir"),
    Verilog(arrayOf("--verilog", "-disable-all-randomization", "-strip-debug-info"), "v"),
}

val firtoolInputFile = chiselOutputDir.file("$modelDir/$modelName.fir")
val firtoolAction = FirtoolOutputAction.IrHw
val firtoolOutputFile = firtoolOutputDir.file("$modelName.${firtoolAction.fileExtension}")

val compileFirrtl = tasks.register<Exec>("compileFirrtl") {
    dependsOn(compileChisel)

    group = "circulator"
    description = "Run firtool on Chisel FIRRTL output"

    inputs.file(firtoolInputFile)
    outputs.file(firtoolOutputFile)

    firtoolOutputFile.asFile.ensureParentDirsCreated()

    commandLine(
        "firtool",
        firtoolInputFile,
        "--format=fir",
        *firtoolAction.flags,
        "-o",
        firtoolOutputFile
    )
}

enum class ArcilatorOutputAction(val flag: String, val fileExtension: String) {
    EmitLlvm("--emit-llvm", "ll"),
    EmitMlir("--emit-mlir", "mlir"),
    EmitBinary("--run", "o"),
}

enum class ArcilatorObserveFlags(val flag: String) {
    ObserveMemories("--observe-memories"),
    ObserveNamedValues("--observe-named-values"),
    ObservePorts("--observe-ports"),
    ObserveRegisters("--observe-registers"),
    ObserveWires("--observe-wires")
}

val arcilatorAction = ArcilatorOutputAction.EmitLlvm
val arcilatorOutputFile = arcilatorOutputDir.file("$modelDir/$modelName.${arcilatorAction.fileExtension}")
val arcilatorOutputStateFile = arcilatorOutputDir.file("$modelDir/$modelName-states.json")

@OptIn(ExperimentalStdlibApi::class)
val arcilatorObserves = enumEntries<ArcilatorObserveFlags>().map { it.flag }.toTypedArray()

val compileCirctMlir = tasks.register<Exec>("compileCirctMlir") {
    dependsOn(compileFirrtl)

    group = "circulator"
    description = "Run arcilator on MLIR output"

    val arcilatorInputFile = firtoolOutputFile
    inputs.file(arcilatorInputFile)
    outputs.file(arcilatorOutputFile)

    arcilatorOutputFile.asFile.ensureParentDirsCreated()

    commandLine(
        "arcilator",
        arcilatorInputFile,
        arcilatorAction.flag,
        *arcilatorObserves,
        "--state-file=${arcilatorOutputStateFile}",
        "-o",
        arcilatorOutputFile
    )
}

sealed interface ClangPlatformBuild {
    val libName: String
    val flags: Array<String>

    // clang -shared -o libcounter.so counter.ll
    class Linux(name: String) : ClangPlatformBuild {
        override val libName: String = "lib$name.so"
        override val flags: Array<String> = TODO("Not yet implemented")
    }

    // clang -isysroot ${xcrun --sdk macosx --show-sdk-path} -nostartfiles -nodefaultlibs -dynamiclib counter.ll -lSystem -o libcounter.dylib
    class Macos(name: String, source: File) : ClangPlatformBuild {
        override val libName: String = "lib$name.dylib"
        override val flags: Array<String> = buildList {
//                add("-isysroot $(xcrun --sdk macosx --show-sdk-path)")
            add("-nostartfiles")
            add("-nodefaultlibs")
            add("-dynamiclib")
            add(source.absolutePath)
            add("-lSystem")
        }.toTypedArray()
    }

    // clang -shared -o counter.dll counter.ll -Wl,/DEF:counter.def
    class Windows(name: String) : ClangPlatformBuild {
        override val libName: String = "$name.dll"
        override val flags: Array<String> = TODO("Not yet implemented")
    }

    companion object {
        fun from(name: String, source: File) = when (val os = OperatingSystem.current()) {
            OperatingSystem.MAC_OS -> Macos(name, source)
            OperatingSystem.LINUX -> Linux(name)
            OperatingSystem.WINDOWS -> Windows(name)
            else -> error("Unsupported OS: ${os.name}")
        }
    }
}

val clangAction = ClangPlatformBuild.from(modelName, arcilatorOutputFile.asFile)
val clangOutputFile = clangOutputDir.file("$modelDir/${clangAction.libName}")

val compileLlvm = tasks.register<Exec>("compileLlvm") {
    dependsOn(compileCirctMlir)

    group = "circulator"
    description = "Run clang on model in LLVM IR"

    inputs.file(arcilatorOutputFile)
    outputs.file(clangOutputFile)

    // might emit warning as receiving llvm code not contains `target triple = "..."` line
    commandLine("clang", *clangAction.flags, "-o", clangOutputFile)
}

val runFullPipeline = tasks.register("runChiselPipeline") {
    group = "circulator"
    description = "Run the full hardware compilation pipeline"
    dependsOn(compileLlvm)
}
