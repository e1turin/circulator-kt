import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

group = "io.github.e1turin.circulator"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.kotlin.link")
    gradlePluginPortal()
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
//    allRules = false
//    config.setFrom("$projectDir/config/detekt.yml")
//    baseline = file("$projectDir/config/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required = true
//        checkstyle.required = true // checkstyle(xml) like format mainly for integrations like Jenkins
        sarif.required = true // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required = true // simple Markdown format
    }
}
