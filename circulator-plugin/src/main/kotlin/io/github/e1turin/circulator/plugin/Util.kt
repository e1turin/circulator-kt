package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.config.ConfigFile
import io.github.e1turin.circulator.config.PluginConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.file.Directory
import java.io.File

public val Project.circulatorDefaultBuildDir: Directory // TODO: support not only KMP (jvmMain)
    get() = layout.buildDirectory.dir("generated/sources/circulator/jvmMain/kotlin/").get()

@OptIn(ExperimentalSerializationApi::class)
public val circulatorJsonFormat: Json = Json {
    allowComments = true
    allowTrailingComma = true
    encodeDefaults = true
    prettyPrint = true
}

internal fun deserializeConfig(file: File): PluginConfig {
    val config = circulatorJsonFormat.decodeFromString<ConfigFile>(file.readText())
    return config
}
