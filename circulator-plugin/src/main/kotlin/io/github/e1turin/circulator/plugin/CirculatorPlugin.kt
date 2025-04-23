package io.github.e1turin.circulator.plugin

import io.github.e1turin.circulator.gen.generateFileSpec
import io.github.e1turin.circulator.interop.arcilator.StateFile
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.File
import java.nio.file.Paths

public class CirculatorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        val circulatorVersion = "0.0.1"
//        project.dependencies.add("implementation", "io.github.e1turin:circulator-core:$circulatorVersion")

        val ext = project.extensions.create("circulator", CirculatorExtension::class.java)

        val genTask = makeGenerateWrappersTask(project, ext)

        project.afterEvaluate {
            project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
                val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
                kotlin.targets.all { target ->
                    val name = target.name
                    if (name == "jvm") {
                        val mainSourceSet = kotlin.sourceSets.getByName("${name}Main")

                        mainSourceSet.kotlin.srcDir(ext.outputDir)
//                        mainSourceSet.kotlin.srcDir(ext.jextractOutputDir)
                        mainSourceSet.resources.srcDir("src/circulator/resources")

//            project.dependencies.add("implementation", project.files(newSourceSet.kotlin.srcDirs))
//            val compileType = KotlinCompile::class.java
                        val compileType = KotlinJvmCompile::class.java

                        project.tasks.withType(compileType).configureEach {
                            it.dependsOn(genTask)
                        }
                    }
                }

            }
//            project.plugins.withType(JavaPlugin::class.java) {
//                val java = project.extensions.getByType(JavaPluginExtension::class.java)
//                val jvmMain = java.sourceSets.getByName("jvmMain")
//                jvmMain.java.srcDir(ext.jextractOutputDir)
//            }
        }

    }

    private fun makeGenerateWrappersTask(
        project: Project,
        extension: CirculatorExtension
    ): TaskProvider<CirculatorGenerateWrapperTask> {
        val generateTask = project.tasks.register(
            "generateCirculatorWrappers",
            CirculatorGenerateWrapperTask::class.java
        ) {
            it.packageName = extension.packageName
            it.stateFile = extension.stateFile
            it.outputDir = extension.outputDir
        }

        return generateTask
    }
}

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


