import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("circulator.kotlin-common-conventions")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_24
        }
    }
}
