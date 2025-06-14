
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api("io.github.e1turin.circulator:circulator-core:0.1.0")
    api(project(":sandbox"))

    api("space.kscience:controls-core:0.4.0-dev-7")
    implementation("space.kscience:controls-magix:0.4.0-dev-7")
    implementation("space.kscience:controls-opcua:0.4.0-dev-7")
    implementation("space.kscience:magix-api:0.4.0-dev-7")
    implementation("space.kscience:magix-server:0.4.0-dev-7")
    implementation("space.kscience:magix-rsocket:0.4.0-dev-7")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.plotlykt.core)
    implementation(libs.plotlykt.server)
    implementation(libs.logback.classic)
    implementation("io.ktor:ktor-client-cio:3.2.0")
    implementation("io.ktor:ktor-client-core:3.2.0")
    api(libs.rsocket.ktor.server)
    api(libs.rsocket.ktor.client)

}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}
