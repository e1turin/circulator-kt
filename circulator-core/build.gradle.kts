plugins {
    id("circulator.kotlin-library-kmp-conventions")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
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
            }
        }
    }
}

mavenPublishing {
    // publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // signAllPublications()

    coordinates(group.toString(), project.name, version.toString())

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
