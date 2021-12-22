/**
 * Copyright 2018 DreamScale, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dreamscale.gradle

import io.freefair.gradle.plugins.lombok.LombokPlugin
import net.jokubasdargis.buildtimer.BuildTimerPlugin
import org.dreamscale.gradle.ide.IdeExtPlugin
import org.dreamscale.gradle.support.ManifestAugmentor
import org.dreamscale.gradle.support.ProjectSupportPlugin

import org.dreamscale.gradle.test.TestExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin

import java.util.jar.JarFile

class CorePlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = 'org.dreamscale.core'

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
        project.pluginManager.apply(IdeExtPlugin)
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
                it.name.startsWith("gradle-core") && it.absolutePath.contains("dreamscale")
            }

            if (gradleCoreJar != null) {
                try {
                    JarFile jarFile = new JarFile(gradleCoreJar)
                    String version = jarFile.manifest.mainAttributes.getValue("Implementation-Version")
                    if (version != null) {
                        project.logger.lifecycle("Using org.dreamscale.core:${version}")
                    }
                } catch (IOException ex) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private boolean shouldPrintGradleCoreVersion() {
        project == project.rootProject && project.hasProperty("dreamscale.printCorePluginVersion")
    }

    private void augmentArtifactManifest() {
        ManifestAugmentor augmentor = new ManifestAugmentor(project)
        augmentor.addGitShaToArtifactManifest()
    }

}
