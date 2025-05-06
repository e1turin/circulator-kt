import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget

plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":sandbox:controls"))
}

kotlin {

}

application {
    mainClass = "io.github.e1turin.circulator.demo.console.MainKt"
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
