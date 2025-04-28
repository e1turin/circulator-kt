package io.github.e1turin.circulator.plugin

import org.gradle.api.file.RegularFileProperty

public abstract class CirculatorExtension {
    public abstract val config: RegularFileProperty
}
