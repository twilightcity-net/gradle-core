package net.twilightcity.gradle

import io.freefair.gradle.plugins.lombok.LombokPlugin
import net.jokubasdargis.buildtimer.BuildTimerPlugin
import net.twilightcity.gradle.support.ManifestAugmentor
import net.twilightcity.gradle.support.ProjectSupportPlugin

import net.twilightcity.gradle.test.TestExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin

import java.util.jar.JarFile

class CorePlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = 'net.twilightcity.core'

    private Project project

    void apply(Project project) {
        this.project = project
        applyJavaExtPlugin()
        applyGroovyPlugin()
        applyProjectDefaultsPlugin()
        applyTestExtPlugin()
        applyIdeExtPlugin()
        applyProjectSupportPlugin()
        applyLombokPlugin()
        applyBuilderTimerPluginIfProjectIsRoot()
        printGradleCorePluginVersion()
        augmentArtifactManifest()
    }

    private void applyJavaExtPlugin() {
        project.pluginManager.apply(JavaExtPlugin)
    }

    private void applyGroovyPlugin() {
        project.pluginManager.apply(GroovyPlugin)
    }

    private void applyIdeExtPlugin() {
        project.pluginManager.apply(net.twilightcity.gradle.ide.IdeExtPlugin)
    }

    private void applyTestExtPlugin() {
        project.pluginManager.apply(TestExtPlugin)
    }

    private void applyProjectSupportPlugin() {
        project.pluginManager.apply(ProjectSupportPlugin)
    }

    private void applyProjectDefaultsPlugin() {
        project.pluginManager.apply(ProjectDefaultsPlugin)
    }

    private void applyLombokPlugin() {
        project.pluginManager.apply(LombokPlugin)
    }

    private void applyBuilderTimerPluginIfProjectIsRoot() {
        if (project == project.rootProject) {
            project.pluginManager.apply(BuildTimerPlugin)
        }
    }

    private String getProjectProperty(String propertyName) {
        project.hasProperty(propertyName) ? project.property(propertyName) : null
    }

    private String getProjectPropertyOrEnvValue(String propertyName, String envKey) {
        String value = getProjectProperty(propertyName)
        if (value == null) {
            value = System.getenv(envKey)
        }
        value
    }

    void printGradleCorePluginVersion() {
        if (shouldPrintGradleCoreVersion()) {
            File gradleCoreJar = project.buildscript.configurations.classpath.find {
                it.name.startsWith("gradle-core") && it.absolutePath.contains("twilightcity")
            }

            if (gradleCoreJar != null) {
                try {
                    JarFile jarFile = new JarFile(gradleCoreJar)
                    String version = jarFile.manifest.mainAttributes.getValue("Implementation-Version")
                    if (version != null) {
                        project.logger.lifecycle("Using net.twilightcity.core:${version}")
                    }
                } catch (IOException ex) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private boolean shouldPrintGradleCoreVersion() {
        project == project.rootProject && project.hasProperty("twilightcity.printCorePluginVersion")
    }

    private void augmentArtifactManifest() {
        ManifestAugmentor augmentor = new ManifestAugmentor(project)
        augmentor.addGitShaToArtifactManifest()
    }

}
