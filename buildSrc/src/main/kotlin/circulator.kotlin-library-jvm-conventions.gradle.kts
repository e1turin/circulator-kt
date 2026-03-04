import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("circulator.kotlin-jvm-conventions")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}