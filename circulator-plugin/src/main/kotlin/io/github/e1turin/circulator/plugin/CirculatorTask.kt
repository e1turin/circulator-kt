package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.config.PluginConfig
import io.github.e1turin.circulator.config.StateType
import io.github.e1turin.circulator.gen.generateFileSpec
import io.github.e1turin.circulator.interop.arcilator.StateFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public abstract class CirculatorGenerateWrappersTask() : DefaultTask() {
    @Internal
    public var config: PluginConfig? = null

    @get:InputFiles
    public var stateFiles: FileCollection? = null

    @OutputDirectory
    public var outputDir: Directory = project.circulatorDefaultBuildDir

    @TaskAction
    public fun generateModelWrappers() {
        logger.debug("stateFiles = {}", stateFiles?.map { it.absolutePath })
        logger.debug("outputDir = {}", outputDir)

        if (stateFiles == null) logger.info("No state files found for generation")
        if (config == null) logger.warn("No config provided")

        config?.models?.forEach { (id, cfg) ->
            logger.debug("Model(modelId = {} packageName = {})", id, cfg.packageName)

            val stateFile = cfg.stateFile
            val json = stateFile.readText()

            val models: StateFile = circulatorJsonFormat.decodeFromString(json)

            for (model in models) {
                val fs = generateFileSpec(model, cfg.packageName) {
                    openModelClass = cfg.modelOptions.open
                    openLibraryClass = cfg.libraryOptions.open
                    // TODO: Propagate config for state types
                    internalStateProjections = cfg.modelOptions.allStatesType.toSet()
                        .containsAll(listOf(StateType.REGISTER, StateType.WIRE, StateType.MEMORY))
                    allStateProjectionsOpen = cfg.modelOptions.allStatesOpen
                    allStateProjectionsMutable = cfg.modelOptions.allStatesMutable
                }
                fs.writeTo(outputDir.asFile.toPath())
            }
        }
    }
}
