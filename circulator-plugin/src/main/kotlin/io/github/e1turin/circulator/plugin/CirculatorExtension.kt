package io.github.e1turin.circulator.plugin

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import javax.inject.Inject

public abstract class CirculatorExtension @Inject constructor(project: Project) {
    public var packageName: String = "io.github.e1turin.circulator.generated"

    public var stateFile: File? = null

    public var outputDir: Directory = project.layout.buildDirectory.dir("generated/sources/circulator/jvmMain/kotlin/").get()
}
