package io.github.e1turin.circulator.demo

import io.github.e1turin.circulator.config.*
import io.github.e1turin.circulator.plugin.circulatorJsonFormat
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File

@ExperimentalSerializationApi
fun main(args: Array<String>) {
    val models = mapOf(
        "counter" to ModelConfig(
            packageName = "io.github.e1turin.circulator.demo.generated",
            stateFile = File("src/jvmMain/resources/arcilator/model-states.json"),
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

val file = File("src/jvmMain/resources/circulator/config.json5")

private fun dumpJson(config: PluginConfig) {
    val json = circulatorJsonFormat.encodeToString(config)
    println(json)
    file.writeText(json)
}

private fun loadJsonConfig(): PluginConfig {
    val json = file.readText()
    val config = circulatorJsonFormat.decodeFromString<PluginConfig>(json)
    return config
}
