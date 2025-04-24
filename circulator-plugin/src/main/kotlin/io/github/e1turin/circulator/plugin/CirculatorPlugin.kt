package io.github.e1turin.circulator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
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

                        mainSourceSet.kotlin.srcDir(ext.outputDir)
                        mainSourceSet.resources.srcDir("src/circulator/resources")
                        p.tasks.withType(KotlinJvmCompile::class.java).configureEach {
                            it.dependsOn(genTask)
                        }
                    }
                }
                /*
                    TODO: move java srcDir configuration to jextract plugin
                    NOTE: This code works; JavaPlugin should be loaded but it has no identifier to use withId(...);
                          it's source sets depend on kotlin plugin in use;
                          there is interesting bug with Kotlin srcDirs in KMP:
                          - https://youtrack.jetbrains.com/issue/KT-66642/KMP-Kotlin-compiler-can-resolve-references-from-Java-code-when-it-should-not
                */
                val java = project.extensions.getByType(JavaPluginExtension::class.java)
                val jvmMain = java.sourceSets.getByName("jvmMain")
                jvmMain.java.srcDir(ext.jextractOutputDir)
            }
        }

    }

    private fun makeGenerateWrappersTask(
        project: Project, extension: CirculatorExtension
    ): TaskProvider<CirculatorGenerateWrapperTask> {
        val generateTask = project.tasks.register(
            "generateCirculatorWrappers", CirculatorGenerateWrapperTask::class.java
        ) {
            it.packageName = extension.packageName
            it.stateFile = extension.stateFile
            it.outputDir = extension.outputDir
        }

        return generateTask
    }
}
