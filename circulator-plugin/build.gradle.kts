import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("kotlin")
    `java-gradle-plugin`
    alias(libs.plugins.gradle.pluginPublish)
    alias(libs.plugins.kotlin.serialization)
}

group = "io.github.e1turin.circulator.plugin"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
}

kotlin {
    explicitApi = ExplicitApiMode.Strict

    jvmToolchain(22)
}

dependencies {
    implementation(project(":circulator-core"))
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinpoet)

    testImplementation(libs.kotlin.test)
}

gradlePlugin {
    plugins {
        create("circulatorPlugin") {
            id = "circulator-plugin"
            implementationClass = "io.github.e1turin.circulator.plugin.CirculatorPlugin"
        }
    }
}
