plugins {
    id("circulator.kotlin-library-jvm-conventions")
    `java-gradle-plugin`
    alias(libs.plugins.gradle.pluginPublish)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":circulator-core"))
    implementation(libs.kotlinGradlePlugin)
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
