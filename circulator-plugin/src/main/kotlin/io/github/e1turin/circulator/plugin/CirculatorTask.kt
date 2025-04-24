package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.gen.generateFileSpec
import io.github.e1turin.circulator.interop.arcilator.StateFile
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Paths
import kotlin.io.readText

public abstract class CirculatorGenerateWrapperTask() : DefaultTask() {

    @Input
    public var packageName: String = "io.github.e1turin.circulator.generated"

    @InputFile
    public var stateFile: File? = null

    @OutputDirectory
    public var outputDir: Directory =
        project.layout.buildDirectory.dir("generated/sources/circulator/jvmMain/kotlin/").get()

    @TaskAction
    public fun generateModelWrappers() {
        logger.debug("packageName = $packageName")
        logger.debug("stateFilePath = ${stateFile?.absolutePath}")
        logger.debug("outputDir = ${outputDir.asFile.absolutePath}")
        logger.debug("jextractOutputDir = ${outputDir.asFile.absolutePath}")

        require(stateFile != null) { "Arcilator's state file must be defined" }

        val json = stateFile!!.readText()

        val models: StateFile = Json.decodeFromString(json)

        for (model in models) {
            // TODO: move configuration to extension
            val fs = generateFileSpec(model, packageName) {
                openModelClass = true
                openLibraryClass = true
                internalStateProjections = true
                allStateProjectionsOpen = true
                allStateProjectionsMutable = true
            }
            fs.writeTo(Paths.get(outputDir.asFile.absolutePath))
        }
    }
}
