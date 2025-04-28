import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("io.github.krakowski.jextract") version "0.5.0"
    id("io.github.e1turin.circulator.plugin")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable(KotlinCompilation.MAIN_COMPILATION_NAME, "counter") {
                mainClass = "io.github.e1turin.circulator.demo.FfmApiTestKt"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)

                implementation("io.github.e1turin.circulator:circulator-core:0.1.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.github.e1turin.circulator:circulator-plugin:0.1.0")
            }
        }
    }
}

circulator {
    config(file("src/jvmMain/resources/circulator/config.json5"))
}

with(tasks) {
    // setup jextract plugin task
    jextract {
        header("${project.projectDir}/src/jvmMain/resources/jextract/dut.h") {
            targetPackage = "io.github.krakowski.jextract.jextracted"
        }
    }

    withType<KotlinJvmCompile>() {
        dependsOn(":sandbox:jextract")
    }

    // allow JVM access native libraries with FFM API
    withType<JavaExec>().configureEach {
        jvmArgs("--enable-native-access=ALL-UNNAMED")

        val dynLibPath = "${projectDir}/src/jvmMain/resources/circulator/lib/counter/"

        when (val host = HostManager.host) {
            // by some reason setting up java.library.path variable do not give result
            KonanTarget.LINUX_X64 -> environment("LD_LIBRARY_PATH", dynLibPath)

            KonanTarget.MINGW_X64 -> jvmArgs("-Djava.library.path=${dynLibPath}")

            KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> environment("DYLD_LIBRARY_PATH", dynLibPath)

            else -> error("Unknown host: $host")
        }
    }
}

java {
    sourceSets {
        val jvmMain by getting {
            // Kakowski's Jextract plugin does not support KMP as I can see
            // NOTE: there is curious bug with Kotlin srcDirs with Java sources in KMP:
            //       - https://youtrack.jetbrains.com/issue/KT-66642/KMP-Kotlin-compiler-can-resolve-references-from-Java-code-when-it-should-not
            val krakowskiJextracted = layout.buildDirectory.dir("generated/sources/jextract/main/java/")
            java.srcDirs(krakowskiJextracted)
        }
    }
}

