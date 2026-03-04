plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "circulator-kt"

include("circulator-core")
include("circulator-plugin")
