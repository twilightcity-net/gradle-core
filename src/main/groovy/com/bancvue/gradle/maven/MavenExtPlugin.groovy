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

import com.bancvue.gradle.categories.ProjectCategory
import com.bancvue.gradle.license.LicenseExtProperties
import com.bancvue.gradle.license.LicenseModel
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.maven.MavenPom
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

@Slf4j
class MavenExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'maven-ext'

	private Project project
	private MavenRepositoryProperties repositoryProperties

	public void apply(Project project) {
		this.project = project
		this.repositoryProperties = new MavenRepositoryProperties(project)
		project.apply(plugin: 'java')
		addArtifactDependencyAndPublishingSupport()
	}

	private void addArtifactDependencyAndPublishingSupport() {
		project.apply(plugin: 'maven')
		createPublishTaskAliases()
		addMavenLocalAndOrganizationArtifactRepository()
		addOrganizationPublishingRepository()
		augmentMavenPom()
		addSourcesJarTask()
		addJavadocJarTask()

		// TODO: need some way to optionally configure source and javadoc as archives
		project.artifacts {
			archives project.sourcesJar
		}
	}

	private void createPublishTaskAliases() {
		createPublishRemoteTaskAsAliasForUploadArchives()
		createPublishTaskAsAliasForInstallTask()
	}

	private void createPublishRemoteTaskAsAliasForUploadArchives() {
		Task uploadArchives = project.tasks.findByName('uploadArchives')
		Task publishRemote = project.tasks.create('publishRemote')
		publishRemote.dependsOn { uploadArchives }
		publishRemote.description = uploadArchives.description
	}

	private void createPublishTaskAsAliasForInstallTask() {
		Task install = project.tasks.findByName('install')
		Task publish = project.tasks.create('publish')
		publish.dependsOn { install }
		publish.description = install.description
	}

	private void addMavenLocalAndOrganizationArtifactRepository() {
		project.repositories.mavenLocal()
		project.repositories {
			maven {
				name repositoryProperties.name
				url repositoryProperties.publicUrl
			}
		}
	}

	private void addOrganizationPublishingRepository() {
		String username = getStringOrEmptyStringIfNull(repositoryProperties.username)
		String password = getStringOrEmptyStringIfNull(repositoryProperties.password)

		project.uploadArchives {
			repositories.mavenDeployer {
				repository(url: repositoryProperties.releaseUrl) {
					authentication(userName: username, password: password)
				}

				snapshotRepository(url: repositoryProperties.snapshotUrl) {
					authentication(userName: username, password: password)
				}
			}
		}
	}

	private static String getStringOrEmptyStringIfNull(String string) {
		string != null ? string : ""
	}

	private String getProjectArtifactId() {
		project.hasProperty('artifactId') ? project.ext.artifactId : null
	}

	private void addSourcesJarTask() {
		Jar sourcesJarTask = ProjectCategory.createJarTask(project, "sourcesJar", "Build", "main", "sources")
		sourcesJarTask.configure {
			from project.sourceSets.main.allSource
		}
	}

	private void addJavadocJarTask() {
		Javadoc javadocTask = project.tasks.getByName('javadoc')
		Jar javadocJarTask = ProjectCategory.createJarTask(project, "javadocJar", "Build", "main", "javadoc")
		javadocJarTask.configure {
			dependsOn { javadocTask }
			from javadocTask.destinationDir
		}
	}

	private void augmentMavenPom() {
		project.uploadArchives {
			repositories.mavenDeployer {
				addBasicDescriptionToMavenPOM(pom)
				attachLicenseToMavenPOMIfLicenseExtPluginApplied(pom)
			}
		}
	}

	private void addBasicDescriptionToMavenPOM(MavenPom pom) {
		pom.project {
			name getProjectArtifactId()
			description project.description
			// TODO: add packaging
			// packaging "jar"
			// TODO: add project url
			// url projectUrl
		}
	}

	private void attachLicenseToMavenPOMIfLicenseExtPluginApplied(MavenPom pom) {
		LicenseExtProperties licenseProperties = new LicenseExtProperties(project)
		LicenseModel licenseModel = licenseProperties.getLicenseModel()

		if (licenseModel != null) {
			attachLicenseModelToMavenPOM(pom, licenseModel)
		} else {
			log.info("No license model found, bypassing augmentation of maven POM with license info")
		}
	}

	private void attachLicenseModelToMavenPOM(MavenPom pom, licenseModel) {
		pom.project {
			licenses {
				license {
					name licenseModel.name
					url licenseModel.url
					distribution licenseModel.distribution
				}
			}
		}
	}

}
