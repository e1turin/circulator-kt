package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.config.PluginConfig
import java.io.File

public abstract class CirculatorExtension {
    public fun config(file: File) {
        config = deserializeConfig(file)
    }

    public var config: PluginConfig = PluginConfig()
        private set
}
