import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.e1turin.circulator"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
}

kotlin {
    explicitApi = ExplicitApiMode.Strict

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(libs.kotlinpoet)
            }
        }
    }
}
