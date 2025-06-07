import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("kotlin")
    `java-gradle-plugin`
    alias(libs.plugins.gradle.pluginPublish)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

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
            id = "io.github.e1turin.circulator.plugin"
            implementationClass = "io.github.e1turin.circulator.plugin.CirculatorPlugin"
        }
    }
}
