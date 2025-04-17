import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization) apply false
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

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }
    sourceSets {
//        val commonMain by getting {
//            dependencies {
//                api(project(":circulator-core"))
//            }
//        }
    }
}

mavenPublishing {
    // publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // signAllPublications()

    coordinates(group.toString(), "circulator-kt", version.toString())

    pom {
        name = "Circulator-kt"
        description = "Circulator Kotlin library for running circuit models."
        inceptionYear = "2025"
        url = "https://github.com/e1turin/circulator-kt"
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id = "e1turin"
                name = "Ivan Turin"
                url = "e1turin.github.io"
            }
        }
        scm {
            url = "https://github.com/e1turin/circulator-kt/"
            connection = "scm:git:git://github.com/e1turin/circulator-kt.git"
            developerConnection = "scm:git:ssh://git@github.com/e1turin/circulator-kt.git"
        }
    }
}
