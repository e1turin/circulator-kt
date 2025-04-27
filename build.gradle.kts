plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
}

allprojects {
    group = "io.github.e1turin.circulator"
    version = "0.1.0"

    repositories {
        mavenCentral()
        google()
    }
}
