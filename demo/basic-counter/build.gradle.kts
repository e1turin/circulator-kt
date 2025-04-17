import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.nio.file.Paths

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.e1turin.circulator"
version = "0.0.1"

//repositories {
//    mavenCentral()
//    mavenLocal()
//}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }

//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        binaries {
//            executable {
//                mainClass = "io.github.e1turin.circulator.demo.Main"
//            }
//        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "io.github.e1turin.circulator.demo.Main"
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
            }

            val circulatorGeneratedDir = layout.buildDirectory.dir("generated/sources/circulator/jvmMain/kotlin/")
            val jextractGeneratedDir = layout.buildDirectory.dir("generated/sources/jextract/jvmMain/java/")

            kotlin.srcDirs(circulatorGeneratedDir, jextractGeneratedDir)
        }
    }
}


tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    val dynLibPath = "${projectDir}/src/jvmMain/resources/lib/"

    when (val host = HostManager.host) {
        // by some reason setting up java.library.path variable do not give result
        KonanTarget.LINUX_X64 -> environment("LD_LIBRARY_PATH", dynLibPath)

        KonanTarget.MINGW_X64 -> jvmArgs("-Djava.library.path=${dynLibPath}")

        KonanTarget.MACOS_X64, KonanTarget.MACOS_ARM64 -> environment("DYLD_LIBRARY_PATH", dynLibPath)

        else -> error("Unknown host: $host")
    }

}
