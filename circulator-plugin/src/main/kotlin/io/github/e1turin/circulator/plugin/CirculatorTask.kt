package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.config.PluginConfig
import io.github.e1turin.circulator.config.StateType
import io.github.e1turin.circulator.gen.generateFileSpec
import io.github.e1turin.circulator.interop.arcilator.StateFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

public abstract class CirculatorGenerateWrappersTask() : DefaultTask() {
    @get:InputFile
    public abstract val configFile: RegularFileProperty

    @get:Internal
    public lateinit var config: PluginConfig

    @get:InputFiles
    public abstract val stateFiles: ListProperty<RegularFile>

    @get:OutputDirectory
    public abstract val outputDir: DirectoryProperty

    @TaskAction
    public fun generateModelWrappers() {
        logger.debug("stateFiles = {}", stateFiles.get().map { it.asFile.absolutePath })
        logger.debug("outputDir = {}", outputDir)

        if (stateFiles.get().isEmpty()) logger.info("No state files found for generation")

        config.models.forEach { (id, cfg) ->
            logger.debug("Model(modelId = {}, packageName = {})", id, cfg.packageName)

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
                fs.writeTo(outputDir.get().asFile.toPath())
            }
        }
    }
}
