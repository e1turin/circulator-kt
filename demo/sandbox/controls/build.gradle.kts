import org.gradle.kotlin.dsl.desktop

plugins {
    kotlin("jvm")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation("space.kscience:controls-core:0.4.0-dev-7")
    implementation("io.github.e1turin.circulator:circulator-core:0.1.0")
    implementation(project(":sandbox"))
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
        mainClass = "io.github.e1turin.circulator.demo.controls.MainKt"
    }
}
