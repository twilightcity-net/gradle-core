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

import org.dreamscale.gradle.ide.IdeExtPlugin
import org.dreamscale.gradle.support.ManifestAugmentor
import org.dreamscale.gradle.support.ProjectSupportPlugin
import org.dreamscale.gradle.test.ComponentTestPlugin

import org.dreamscale.gradle.test.TestExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

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
        applyComponentTestPlugin()
        applyIdeExtPlugin()
        applyProjectSupportPlugin()
        applyBuilderTimerPluginIfProjectIsRoot()
        printGradleCorePluginVersion()
        augmentArtifactManifest()
    }

    private void applyJavaExtPlugin() {
        project.apply(plugin: JavaExtPlugin.PLUGIN_NAME)
    }

    private void applyGroovyPlugin() {
        project.apply(plugin: 'groovy')
    }

    private void applyIdeExtPlugin() {
        project.apply(plugin: IdeExtPlugin.PLUGIN_NAME)
    }

    private void applyTestExtPlugin() {
        project.apply(plugin: TestExtPlugin.PLUGIN_NAME)
    }

    private void applyComponentTestPlugin() {
        project.apply(plugin: ComponentTestPlugin.PLUGIN_NAME)
    }

    private void applyProjectSupportPlugin() {
        project.apply(plugin: ProjectSupportPlugin.PLUGIN_NAME)
    }

    private void applyProjectDefaultsPlugin() {
        project.apply(plugin: ProjectDefaultsPlugin.PLUGIN_NAME)
    }

    private void applyBuilderTimerPluginIfProjectIsRoot() {
        if (project == project.rootProject) {
            project.apply(plugin: "net.jokubasdargis.build-timer")
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
