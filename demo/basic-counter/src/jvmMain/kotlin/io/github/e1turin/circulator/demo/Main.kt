package io.github.e1turin.circulator.demo

import io.github.e1turin.circulator.arcilator.StateFile
import io.github.e1turin.circulator.model.generateFileSpec
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            val json = this::class.java.getResource("model-states.json")?.readText()
            val json = File("src/jvmMain/resources/arcilator/model-states.json").readText()

            val models: StateFile = Json.decodeFromString(json)

            println(models[0])

            val fs = generateFileSpec(models[0], "io.github.e1turin.circulator.demo.generated") {
                openModelClass = true
                openLibraryClass = true
                internalStateProjections = true
                allStateProjectionsOpen = true
                allStateProjectionsMutable = true
            }
            // add source set in build.gradle.kts
            fs.writeTo(Paths.get("build/generated/sources/circulator/jvmMain/kotlin/"))

            playWithFFM()
        }
    }
}
