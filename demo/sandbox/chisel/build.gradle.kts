plugins {
    scala
}

repositories {
    mavenCentral()
}

// Scala and Chisel configuration
dependencies {
    implementation("org.scala-lang:scala-library:2.13.15")
    implementation("org.chipsalliance:chisel_2.13:6.7.0")
    scalaCompilerPlugins("org.chipsalliance:chisel-plugin_2.13.15:6.7.0")
    implementation("org.chipsalliance:firtool-resolver_2.13:2.0.1")
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

fun app(name: String, mainClass: String, jvmArgs: List<String> = emptyList()) {
    val formattedName = name.split("\\s+".toRegex()).joinToString("") {
            it.lowercase().replaceFirstChar { c -> c.uppercase() }
        }

    tasks.register<JavaExec>("run${formattedName}") {
        group = "application"
        description = "Run app $name"
        classpath = sourceSets["main"].runtimeClasspath
        this.mainClass = mainClass
        this.jvmArgs = listOf("-Dsome.flag=true") // optional
    }
}

app("Chisel Counter FIRRTL", mainClass = "io.github.e1turin.circulator.demo.counter.Main")
app("Chisel Example FIRRTL", mainClass = "io.github.e1turin.circulator.demo.example.Main")
