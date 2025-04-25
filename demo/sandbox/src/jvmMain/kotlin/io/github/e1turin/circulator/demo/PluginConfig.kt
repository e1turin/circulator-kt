package io.github.e1turin.circulator.demo

import io.github.e1turin.circulator.config.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File

@ExperimentalSerializationApi
fun main(args: Array<String>) {
    val models = mapOf(
        "counter" to ModelConfig(
            packageName = "io.github.e1turin.circulator.demo.generated",
            stateFilePath = "src/jvmMain/resources/arcilator/model-states.json",
            outputDirPath = "build/generated/sources/circulator/jvmMain/kotlin",
            modelOptions = ModelOptions(
                open = true,
                allStatesOpen = true,
                allStatesMutable = true,
                allStatesType = StateType.entries,
                states = mapOf(
                    "clkInternal" to StateAttributes(
                        open = true,
                        mutable = true,
                        access = true
                    )
                )
            ),
            libraryOptions = LibraryOptions(
                open = true,
            )
        )
    )

    val config = PluginConfig(models)

    dumpJson(config)

    val loadedConfig = loadJsonConfig().also { println("Loaded config: $it") }

    assert(loadedConfig == config)
}

@OptIn(ExperimentalSerializationApi::class)
val jsonFormat = Json {
    encodeDefaults = true
    prettyPrint = true
    allowComments = true
}

val file = File("src/jvmMain/resources/circulator/config.json5")

private fun dumpJson(config: PluginConfig) {
    val json = jsonFormat.encodeToString(config)
    println(json)
    file.writeText(json)
}

private fun loadJsonConfig(): PluginConfig {
    val json = file.readText()
    val config = jsonFormat.decodeFromString<PluginConfig>(json)
    return config
}
