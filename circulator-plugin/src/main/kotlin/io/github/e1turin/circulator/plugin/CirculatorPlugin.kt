package io.github.e1turin.circulator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import kotlin.collections.component1
import kotlin.collections.component2


public class CirculatorPlugin : Plugin<Project> {
    private lateinit var outputGenKotlinDir: Directory // TODO: refactor for kotlin project type

    override fun apply(project: Project) {
        val ext = project.extensions.create("circulator", CirculatorExtension::class.java)

        val genTask = makeGenerateKotlinClassesTask(project, ext)
        outputGenKotlinDir = project.circulatorDefaultJvmBuildDir

        project.afterEvaluate { p ->
            p.setupKotlinMultiplatform(genTask)
            p.setupKotlinJvm(genTask)
        }
    }

    /**
     * Try to apply config for KMP project
     */
    private fun <T : Task> Project.setupKotlinMultiplatform(
        genTask: TaskProvider<T>,
    ) = plugins.withId("org.jetbrains.kotlin.multiplatform") {

        val kmp = extensions.getByType(KotlinMultiplatformExtension::class.java)

        kmp.targets.withType(KotlinJvmTarget::class.java) {
            val mainSourceSet = kmp.sourceSets.getByName("${it.name}Main")

            outputGenKotlinDir = project.circulatorDefaultKmpBuildDir
            mainSourceSet.kotlin.srcDir(outputGenKotlinDir!!)
//            do I really need resources?
//            mainSourceSet.resources.srcDir(project.circulatorDefaultResourcesDir)

            makeKotlinJvmCompileTaskDependOn(genTask)
        }
    }

    /**
     * Try to apply config for JVM project
     */
    private fun <T : Task> Project.setupKotlinJvm(
        genTask: TaskProvider<T>,
    ) = plugins.withId("org.jetbrains.kotlin.jvm") {
        val kotlinJvm = extensions.getByType(KotlinJvmExtension::class.java)
        val mainSourceSet = kotlinJvm.sourceSets.getByName("main")

        outputGenKotlinDir = project.circulatorDefaultJvmBuildDir
        mainSourceSet.kotlin.srcDir(outputGenKotlinDir!!)
        makeKotlinJvmCompileTaskDependOn(genTask)
    }

    private fun <T : Task> Project.makeKotlinJvmCompileTaskDependOn(
        task: TaskProvider<T>
    ) {
        tasks.withType(KotlinJvmCompile::class.java).configureEach {
            it.dependsOn(task)
        }
    }

    private fun makeGenerateKotlinClassesTask(
        project: Project,
        ext: CirculatorExtension
    ): TaskProvider<CirculatorGenerateWrappersTask> {
        val generateTask = project.tasks.register(
            "generateKotlinClasses", CirculatorGenerateWrappersTask::class.java
        ) { task ->
            task.group = "circulator"

            task.configFile.set(ext.config)

            val config = deserializeConfig(ext.config.asFile.get())
            task.config = config

            val mentionedStateFiles = config.models.map { (_, model) ->
                val fileProvider = project.provider { model.stateFile }
                val regularFileProvider = project.layout.file(fileProvider)
                regularFileProvider.get()
            }
            task.stateFiles.set(mentionedStateFiles)

            task.outputDir.set(outputGenKotlinDir)
        }

        return generateTask
    }
}
