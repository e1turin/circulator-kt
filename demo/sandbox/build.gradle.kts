import io.github.e1turin.circulator.config.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("circulator-plugin")
}

group = "io.github.e1turin.circulator"
version = "0.0.1"

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "pluginConfig") {
                mainClass = "io.github.e1turin.circulator.demo.PluginConfigKt"
            }
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "counter") {
                mainClass = "io.github.e1turin.circulator.demo.FfmApiTestKt"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)

                implementation("io.github.e1turin.circulator:circulator-core:0.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.e1turin.circulator.plugin:circulator-plugin:0.0.1")
            }
        }
    }
}

val circulatorConfig = PluginConfig(
    models = mapOf(
        "counter" to ModelConfig(
            packageName = "io.github.e1turin.circulator.demo.generated",
            stateFile = File("src/jvmMain/resources/arcilator/model-states.json"),
            outputDirPath = "build/generated/sources/circulator/jvmMain/kotlin",
            modelOptions = ModelOptions(
                open = true,
                allStatesOpen = true,
                allStatesMutable = true,
                allStatesType = listOf(StateType.INPUT, StateType.OUTPUT),
                states = mapOf(
                    "clkInternal" to StateAttributes(
                        open = true,
                        mutable = true,
                        access = true
                    )
                )
            ),
            libraryOptions = LibraryOptions(
                open = true,
            )
        )
    )
)
circulator {
    config(file("src/jvmMain/resources/circulator/config.json5"))
    // or else
    // config(circulatorConfig)
}

java {
    sourceSets {
        val jvmMain by getting {
            /*
                TODO: move Java srcDir configuration to Jextract plugin
                NOTE: there is curious bug with Kotlin srcDirs with Java sources in KMP:
                      - https://youtrack.jetbrains.com/issue/KT-66642/KMP-Kotlin-compiler-can-resolve-references-from-Java-code-when-it-should-not
            */
            val jextracted = layout.buildDirectory.dir("generated/sources/jextract/jvmMain/java/")
            java.srcDir(jextracted)
        }
    }
}

// allow JVM access native libraries with FFM API
tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    val dynLibPath = "${projectDir}/src/jvmMain/resources/lib/counter/"

    when (val host = HostManager.host) {
        // by some reason setting up java.library.path variable do not give result
        KonanTarget.LINUX_X64 -> environment("LD_LIBRARY_PATH", dynLibPath)

        KonanTarget.MINGW_X64 -> jvmArgs("-Djava.library.path=${dynLibPath}")

        KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> environment("DYLD_LIBRARY_PATH", dynLibPath)

        else -> error("Unknown host: $host")
    }

}

