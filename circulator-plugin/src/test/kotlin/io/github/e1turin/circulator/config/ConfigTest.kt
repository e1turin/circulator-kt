package io.github.e1turin.circulator.config

import io.github.e1turin.circulator.plugin.circulatorJsonFormat
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigTest {
    @Test
    fun `deserialize serialized config`() {
        val encodedJsonString = circulatorJsonFormat.encodeToString(sampleConfig)
        val decodedJsonConfig = circulatorJsonFormat.decodeFromString<PluginConfig>(encodedJsonString)
        assertEquals(sampleConfig, decodedJsonConfig)
    }

    private val sampleConfig = PluginConfig(
        mapOf(
            "counter" to ModelConfig(
                packageName = "io.github.e1turin.circulator.demo.generated",
                stateFile = File("src/jvmTest/resources/arcilator/model-states.json"),
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
    )
}
