package io.github.e1turin.circulator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import kotlin.collections.component1
import kotlin.collections.component2


public class CirculatorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("circulator", CirculatorExtension::class.java)

        val genTask = makeGenerateWrappersTask(project, ext)

        project.afterEvaluate { p ->
            p.plugins.withId("org.jetbrains.kotlin.multiplatform") {
                p.setupKotlinMultiplatform(genTask)
                // TODO: setupKotlinJvm
            }
        }
    }

    private fun <T: Task> Project.setupKotlinMultiplatform(
        genTask: TaskProvider<T>,
    ) {
        val kmp = extensions.getByType(KotlinMultiplatformExtension::class.java)

        kmp.targets.withType(KotlinJvmTarget::class.java) {
            val mainSourceSet = kmp.sourceSets.getByName("${it.name}Main")

            mainSourceSet.kotlin.srcDir(project.circulatorDefaultBuildDir)
//            do I really need resources?
//            mainSourceSet.resources.srcDir(project.circulatorDefaultResourcesDir)

            makeKotlinJvmCompileTaskDependOn(genTask)
        }
    }

    private fun <T: Task> Project.makeKotlinJvmCompileTaskDependOn(
        task: TaskProvider<T>
    ) {
        tasks.withType(KotlinJvmCompile::class.java).configureEach {
            it.dependsOn(task)
        }
    }

    private fun makeGenerateWrappersTask(
        project: Project,
        ext: CirculatorExtension
    ): TaskProvider<CirculatorGenerateWrappersTask> {
        val generateTask = project.tasks.register(
            "circulatorGenWrappers", CirculatorGenerateWrappersTask::class.java
        ) { task ->
            task.configFile.set(ext.config)

            val config = deserializeConfig(ext.config.asFile.get())
            task.config = config

            val mentionedStateFiles = config.models.map { (_, model) ->
                val fileProvider = project.provider { model.stateFile }
                val regularFileProvider = project.layout.file(fileProvider)
                regularFileProvider.get()
            }
            task.stateFiles.set(mentionedStateFiles)
        }

        return generateTask
    }
}
