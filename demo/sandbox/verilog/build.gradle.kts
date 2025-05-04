import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import kotlin.enums.enumEntries

plugins {
    // project for running verilog compilation pipeline
}

repositories {
    mavenCentral()
}

dependencies {
}

val generatedDir = layout.buildDirectory.dir("generated/sources/circulator")

val arcilatorOutputDir = generatedDir.get().dir("arcilator")
val clangOutputDir = generatedDir.get().dir("clang")
val verilogOutputDir = generatedDir.get().dir("circt-verilog")

enum class CirctVerilogAction(val flag: String, val fileExtension: String) {
    IrHw("--ir-hw", "hw.mlir"),
    IrMoore("--ir-moore", "moore.mlir"),
    IrLlhd("--ir-llhd", "llhd.mlir"),
}

val verilogInputFile = layout.projectDirectory.file("src/main/verilog/counter/counter.v")
val verilogAction = CirctVerilogAction.IrHw
val verilogOutputFile = verilogOutputDir.file("counter/counter.${verilogAction.fileExtension}")

// this task is broken, as circt-verilog produces wrong output - it forgets comb.add instruction
val compileVerilog = tasks.register<Exec>("compileVerilog(broken)") {
    group = "circulator"
    description = "Run circt-verilog on Verilog sources"

    inputs.file(verilogInputFile)
    outputs.file(verilogOutputFile)

    verilogOutputFile.asFile.ensureParentDirsCreated()

    commandLine(
        "circt-verilog",
        verilogInputFile,
        "--format=sv",
        verilogAction.flag,
        "-o",
        verilogOutputFile
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
val arcilatorOutputFile = arcilatorOutputDir.file("counter/Counter.${arcilatorAction.fileExtension}")
val arcilatorOutputStateFile = arcilatorOutputDir.file("counter/counter-states.json")

@OptIn(ExperimentalStdlibApi::class)
val arcilatorObserves = enumEntries<ArcilatorObserveFlags>().map { it.flag }.toTypedArray()

val compileCirctMlir = tasks.register<Exec>("compileCirctMlir") {
    dependsOn(compileVerilog)

    group = "circulator"
    description = "Run arcilator on MLIR output"

    val arcilatorInputFile = verilogOutputFile
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

val clangAction = ClangPlatformBuild.from("counterverilog", arcilatorOutputFile.asFile)
val clangOutputFile = clangOutputDir.file("counter/${clangAction.libName}")

val compileLlvm = tasks.register<Exec>("compileLlvm") {
    dependsOn(compileCirctMlir)

    group = "circulator"
    description = "Run clang on model in LLVM IR"

    inputs.file(arcilatorOutputFile)
    outputs.file(clangOutputFile)

    // might emit warning as receiving llvm code not contains `target triple = "..."` line
    commandLine("clang", *clangAction.flags, "-o", clangOutputFile)
}

val runFullPipeline = tasks.register("runVerilogPipeline") {
    group = "circulator"
    description = "Run the full hardware compilation pipeline"
    dependsOn(compileLlvm)
}
