import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("circulator.kotlin-kmp-conventions")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}
