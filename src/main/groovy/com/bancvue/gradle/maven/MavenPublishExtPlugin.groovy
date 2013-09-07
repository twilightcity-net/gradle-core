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
package com.bancvue.gradle.maven

import com.bancvue.gradle.GradlePluginMixin
import com.bancvue.gradle.test.TestExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

@Mixin(GradlePluginMixin)
class MavenPublishExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'maven-publish-ext'

	private Project project
	private MavenRepositoryProperties repositoryProperties

	public void apply(Project project) {
		this.project = project
		this.repositoryProperties = new MavenRepositoryProperties(project)
		project.apply(plugin: 'java')
		addArtifactDependencyAndPublishingSupport()
		setJarBaseNameToArtifactIdIfArtifactIdSet()
	}

	private void addArtifactDependencyAndPublishingSupport() {
		project.apply(plugin: 'maven-publish')
		renamePublishTasks()
		addNexusDependencyRepository()
		addNexusPublishingRepository()
		addSourceJarTask()
		addJavadocJarTask()
		addProjectPublication()
	}

	private void renamePublishTasks() {
		renamePublishTaskToPublishRemote()
		renamePublishToMavenLocalTaskToPublish()
	}

	private void renamePublishTaskToPublishRemote() {
		Task publish = project.tasks.findByName('publish')
		project.tasks.remove(publish)
		publish.description = 'Publishes all publications produced by this project.'
		renameTask(publish, 'publishRemote')
	}

	private void renamePublishToMavenLocalTaskToPublish() {
		Task publishToMavenLocal = project.tasks.findByName('publishToMavenLocal')
		publishToMavenLocal.description = 'Publishes all Maven publications produced by this project to the local Maven cache.'
		renameTask(publishToMavenLocal, 'publish')
	}

	private void addNexusDependencyRepository() {
		project.repositories.mavenLocal()
		project.repositories {
			maven {
				name repositoryProperties.name
				url repositoryProperties.publicUrl
			}
		}
	}

	private boolean isSnapshotProject() {
		project.version =~ /SNAPSHOT$/
	}

	private String acquireNexusPublishUrl() {
		if (isSnapshotProject()) {
			repositoryProperties.snapshotUrl
		} else {
			repositoryProperties.releaseUrl
		}
	}

	private void addNexusPublishingRepository() {
		project.publishing {
			repositories {
				maven {
					name repositoryProperties.name
					url acquireNexusPublishUrl()
					if (repositoryProperties.hasCredentialsDefined()) {
						credentials {
							username repositoryProperties.username
							password repositoryProperties.password
						}
					}
				}
			}
		}
	}

	private String getBaseNameForTask(Jar task) {
		String baseName = getProjectArtifactId()
		if (baseName == null) {
			baseName = task.baseName
		}
		baseName
	}

	private String getProjectArtifactId() {
		project.hasProperty('artifactId') ? project.ext.artifactId : null
	}

	private String getProjectName() {
		String projectName = getProjectArtifactId()
		if (projectName == null) {
			projectName = project.name
		}
		projectName.replaceAll(/[-](\S)/) { it[1].toUpperCase() }
	}

	private void addSourceJarTask() {
		Jar sourceJarTask = project.tasks.create("sourceJar", Jar)
		sourceJarTask.configure {
			group = "Build"
			baseName = getBaseNameForTask(sourceJarTask)
			classifier = "sources"
			description = "Assembles a jar archive containing the main sources."
			from project.sourceSets.main.allSource
		}
	}

	private void addJavadocJarTask() {
		Javadoc javadocTask = project.tasks.getByName('javadoc')
		Jar javadocJarTask = project.tasks.create("javadocJar", Jar)
		javadocJarTask.configure {
			dependsOn javadocTask
			group = "Build"
			baseName = getBaseNameForTask(javadocJarTask)
			classifier = "javadoc"
			description = "Assembles a jar archive containing the main javadocs."
			from javadocTask.destinationDir
		}
	}

	private void addProjectPublication() {
		if (!project.hasProperty("customPublication")) {
			project.publishing {
				publications {
					"${getProjectName()}"(MavenPublication) {
						from project.components.java
						if (getProjectArtifactId() != null) {
							pom.projectIdentity.artifactId = getProjectArtifactId()
						}
						attachAdditionalArtifactsToMavenPublication(delegate)
					}
				}
			}
		}
	}

	private void attachAdditionalArtifactsToMavenPublication(MavenPublication publication) {
		attachArtifactToMavenPublication(publication, "sourceJar")
//		attachArtifactToMavenPublication(publication, "javadocJar")
		attachTestArtifactToMavenPublicationIfMainTestConfigurationDefined(publication)
	}

	private void attachArtifactToMavenPublication(MavenPublication publication, String jarTaskName) {
		Task sourceJarTask = project.tasks.getByName(jarTaskName)
		publication.artifact(sourceJarTask)
	}

	private void attachTestArtifactToMavenPublicationIfMainTestConfigurationDefined(MavenPublication publication) {
		Jar mainTestJarTask = TestExtPlugin.getMainTestJarTaskOrNullIfMainTestConfigurationNotDefined(project)
		if (mainTestJarTask != null) {
			mainTestJarTask.baseName = getBaseNameForTask(mainTestJarTask)
			publication.artifact(mainTestJarTask)
		}
	}

	private void setJarBaseNameToArtifactIdIfArtifactIdSet() {
		String artifactId = getProjectArtifactId()
		if (artifactId != null) {
			project.jar.baseName = artifactId
		}
	}

}
