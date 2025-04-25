package io.github.e1turin.circulator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile


public class CirculatorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("circulator", CirculatorExtension::class.java)

        val genTask = makeGenerateWrappersTask(project, ext)

        project.afterEvaluate { p ->
            p.plugins.withId("org.jetbrains.kotlin.multiplatform") {
                val kotlin = p.extensions.getByType(KotlinMultiplatformExtension::class.java)

                kotlin.targets.all { target ->
                    val name = target.name

                    if (name == "jvm") {
                        val mainSourceSet = kotlin.sourceSets.getByName("${name}Main")

                        mainSourceSet.kotlin.srcDir(project.circulatorDefaultBuildDir)
                        mainSourceSet.resources.srcDir("src/circulator/resources")

                        p.tasks.withType(KotlinJvmCompile::class.java).configureEach {
                            it.dependsOn(genTask)
                        }
                    }
                }
            }
        }
    }

    private fun makeGenerateWrappersTask(
        project: Project,
        extension: CirculatorExtension
    ): TaskProvider<CirculatorGenerateWrappersTask> {
        val generateTask = project.tasks.register(
            "circulatorGenWrappers", CirculatorGenerateWrappersTask::class.java
        ) { task ->
            extension.config?.let { config ->
                val stateFiles = config.models.map { (id, model) -> model.stateFile.name }

                task.stateFiles = project.files(*stateFiles.toTypedArray()) // TODO: check reaction
                task.config = config
            } ?: throw IllegalArgumentException("Circulator config could not be found")
        }

        return generateTask
    }
}
