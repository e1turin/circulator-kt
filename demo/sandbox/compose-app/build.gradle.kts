import org.gradle.kotlin.dsl.desktop
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    kotlin("jvm")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":sandbox:controls"))
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
}

kotlin {

}

compose.desktop {
    application {
        mainClass = "io.github.e1turin.circulator.demo.compose.MainKt"
        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }
}

fun JavaForkOptions.setupLibraryPath() {
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    val dynLibPath = "${projectDir}/../chisel/build/generated/sources/circulator/clang/counter"
    when (val host = HostManager.host) {
        // by some reason setting the java.library.path variable does not lead to the goal
        KonanTarget.LINUX_X64 -> environment("LD_LIBRARY_PATH", dynLibPath)

        KonanTarget.MINGW_X64 -> jvmArgs("-Djava.library.path=${dynLibPath}")

        KonanTarget.MACOS_X64 -> environment("DYLD_LIBRARY_PATH", dynLibPath)
        KonanTarget.MACOS_ARM64 -> environment("DYLD_LIBRARY_PATH", dynLibPath)

        else -> error("Unknown host: $host")
    }
}

tasks.withType<JavaExec>().configureEach {
    standardInput = System.`in`
    setupLibraryPath()
}

tasks.withType<Test>().configureEach {
    setupLibraryPath()
}
