package io.github.e1turin.circulator.gen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec


fun interface FileGenerator {
    fun fileSpec(): FileSpec
}

fun interface InterfaceGenerator {
    fun interfaceSpec(): TypeSpec
}

fun interface ClassGenerator {
    fun classSpec(): TypeSpec
}

fun interface ClassCompanionGenerator {
    fun companionSpec(): TypeSpec
}

fun interface PropertyGenerator {
    fun propertySpec(): PropertySpec
}
