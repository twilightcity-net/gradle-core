/**
 * Copyright 2013 BancVue, LTD
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
package org.dreamscale.gradle.custom

import org.dreamscale.gradle.ResourceResolver
import org.dreamscale.gradle.maven.publish.MavenPublishExtPlugin
import org.dreamscale.gradle.maven.MavenRepositoryProperties
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.wrapper.Wrapper

class CustomGradlePlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'org.dreamscale.custom-gradle'

	private Project project
	private CustomGradleProperties gradleProperties

	void apply(Project project) {
		this.project = project
		gradleProperties = new CustomGradleProperties(project)
		applyJavaPlugin()
		applyMavenPublishExtPluginAndDisableMainPublication()
		addBuildCustomGradleDistroTask()
		addMavenPublication()
		addCustomWrapperTask()
	}

	private void applyJavaPlugin() {
		project.apply(plugin: 'java')
	}

	private void applyMavenPublishExtPluginAndDisableMainPublication() {
		project.apply(plugin: MavenPublishExtPlugin.PLUGIN_NAME)

		project.publishing_ext {
			publication("main") {
				enabled false
			}
		}
	}

	private DownloadGradle addDownloadGradleTask() {
		DownloadGradle downloadGradleTask = project.tasks.create('downloadGradle', DownloadGradle)
		downloadGradleTask.configure {
			description = 'Download Gradle version from Gradle distributions website'
			gradleVersion = gradleProperties.baseVersion
			destinationDir = project.file("${project.buildDir}/gradle-downloads")
		}
		downloadGradleTask
	}

	private void addBuildCustomGradleDistroTask() {
		DownloadGradle downloadGradleTask = addDownloadGradleTask()
		File gradleInitializationScript = new File(project.buildDir, 'tmp/customized.gradle')
		Task writeInitializationScriptContentTask = addwriteInitializationScriptContentTask(gradleInitializationScript)
		Task buildCustomGradleDistroTask = project.tasks.create('buildCustomGradleDistro', Zip)
		buildCustomGradleDistroTask.configure {
			dependsOn { [downloadGradleTask, writeInitializationScriptContentTask] }
			doFirst { println "configure internal zip" }
			group = 'Build'
			description = 'Add extra files to company Gradle distribution'
			baseName = gradleProperties.artifactId
			version = gradleProperties.version
			classifier = 'bin'
			from project.zipTree(downloadGradleTask.destinationFile)
			into(downloadGradleTask.distributionNameBase) {
				into('init.d') {
					from gradleInitializationScript
				}
			}
		}
	}

	Task addwriteInitializationScriptContentTask(File file) {
		project.tasks.create('writeInitializationScriptContent') {
			doLast {
				ResourceResolver resolver = ResourceResolver.create(project)
				String content = resolver.getResourceContent(gradleProperties.scriptResourcePath)
				if (content == null || content.trim().isEmpty()) {
					throw new GradleException("No content found at resource path=${gradleProperties.scriptResourcePath}")
				}

				file.parentFile.mkdirs()
				file.write(content)
			}
		}
	}

	private void addMavenPublication() {
		project.publishing {
			Task buildCustomGradleDistroTask = project.tasks.getByName('buildCustomGradleDistro')
			publications {
				"customGradleDistro"(MavenPublication) {
					artifact buildCustomGradleDistroTask
					groupId = gradleProperties.groupId
					artifactId = gradleProperties.artifactId
					version = gradleProperties.version
				}
			}
		}
	}

	private void addCustomWrapperTask() {
		Task wrapper = project.tasks.create('customWrapper', Wrapper)
		wrapper.group = 'Utilities'
		// TODO: add description
		wrapper.distributionUrl = createDistributionUrl()
	}

	private void createDistributionUrl() {
		MavenRepositoryProperties repository = new MavenRepositoryProperties(project)
		// TODO: will fail if gradleProperties.groupId is null, need test and need to figure out what to do in that case
		"${repository.releaseUrl}/" +
				"${gradleProperties.groupId.replaceAll('.', '/')}/" +
				"${gradleProperties.artifactId}/${gradleProperties.version}/" +
				"${gradleProperties.artifactId}-${gradleProperties.version}-bin.zip"
	}
}
