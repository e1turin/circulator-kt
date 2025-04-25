package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.config.PluginConfig
import org.gradle.api.Project
import java.io.File
import javax.inject.Inject

public abstract class CirculatorExtension @Inject constructor(project: Project) {
    public fun config(file: File) {
        check(config == null) { "Circulator is already configured" }
        config = deserializeConfig(file)
    }

    public fun config(setup: PluginConfig) {
        check(config == null) { "Circulator is already configured" }
        config = setup
    }

    public var config: PluginConfig? = null
        private set
}
