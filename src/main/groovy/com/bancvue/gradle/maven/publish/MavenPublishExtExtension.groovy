/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.maven.publish

import com.bancvue.gradle.license.LicenseExtPlugin
import com.bancvue.gradle.license.LicenseExtProperties
import com.bancvue.gradle.license.LicenseModel
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.publish.maven.MavenPublication

@Slf4j
class MavenPublishExtExtension {

	static final String NAME = "publishing_ext"

	private Configurator publicationConfigurator

	MavenPublishExtExtension(Project project) {
		this.publicationConfigurator = new Configurator(project)

		Configurator configurator = publicationConfigurator
		project.publishing {
			publications {
				configurator.getPublicationsToApply().each { ExtendedPublication extendedPublication ->
					extendedPublication.deriveUnsetVariables()

					"${extendedPublication.name}"(MavenPublication) { MavenPublication mavenPublication ->
						configurator.configureMavenPublication(extendedPublication, mavenPublication)
					}
				}
			}
		}
	}

	void publication(String id, Closure configure = null) {
		publicationConfigurator.addPublication(id, configure)
	}


	private static class Configurator {

		private Project project
		private Map<String,ExtendedPublication> publicationMap = [:]

		Configurator(Project project) {
			this.project = project
		}

		void addPublication(String id, Closure configure = null) {
			ExtendedPublication publication = createExtendedPublication(id, configure)
			publicationMap.put(id, publication)
		}

		private ExtendedPublication createExtendedPublication(String id, Closure configure) {
			ExtendedPublication publication = new ExtendedPublication(id, project)

			if (configure != null) {
				publication.configure(configure)
			}
			publication
		}

		List<ExtendedPublication> getPublicationsToApply() {
			if (!publicationMap.containsKey("main")) {
				addPublication("main")
			}

			publicationMap.values().findAll { ExtendedPublication publication ->
				publication.enabled
			}
		}

		private void configureMavenPublication(ExtendedPublication extendedPublication, MavenPublication mavenPublication) {
			mavenPublication.artifact(extendedPublication.archiveTask)
			if (extendedPublication.artifactId != null) {
				mavenPublication.artifactId = extendedPublication.artifactId
			}
			addBasicDescriptionToMavenPOM(extendedPublication, mavenPublication)
			attachLicenseToMavenPOMIfLicenseExtPluginApplied(mavenPublication)
			attachAdditionalArtifactsToMavenPublication(extendedPublication, mavenPublication)
			attachDependenciesToMavenPublication(extendedPublication, mavenPublication)
		}

		private void addBasicDescriptionToMavenPOM(ExtendedPublication extendedPublication, MavenPublication mavenPublication) {
			mavenPublication.pom.withXml {
				asNode().children().last() + {
					name extendedPublication.artifactId
					// TODO: add description
					// description project.description
					// TODO: add project url
					// url projectUrl
				}
			}
		}

		private void attachLicenseToMavenPOMIfLicenseExtPluginApplied(MavenPublication mavenPublication) {
			if (project.getPlugins().findPlugin(LicenseExtPlugin)) {
				LicenseExtProperties licenseProperties = new LicenseExtProperties(project)
				LicenseModel licenseModel = licenseProperties.getLicenseModel()

				if (licenseModel != null) {
					attachLicenseModelToMavenPOM(mavenPublication, licenseModel)
				} else {
					log.warn("license-ext plugin applied but no license model found, bypassing augmentation of maven POM with license info")
				}
			} else {
				log.info("license-ext plugin not applied, bypassing augmentation of maven POM with license info")
			}
		}

		private void attachLicenseModelToMavenPOM(MavenPublication publication, LicenseModel licenseModel) {
			publication.pom.withXml {
				asNode().children().last() + {
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

		private void attachAdditionalArtifactsToMavenPublication(ExtendedPublication extendedPublication, MavenPublication mavenPublication) {
			if (extendedPublication.publishSources) {
				mavenPublication.artifact(extendedPublication.sourcesArchiveTask)
			}
		}

		private void attachDependenciesToMavenPublication(ExtendedPublication extendedPublication, MavenPublication mavenPublication) {
			DependencySet allDependencies = extendedPublication.runtimeConfiguration.allDependencies

			mavenPublication.pom.withXml {
				asNode().children().last() + {
					dependencies {
						allDependencies.each { Dependency aDependency ->
							dependency {
								groupId aDependency.group
								artifactId aDependency.name
								version aDependency.version
								scope "runtime"
							}
						}
					}
				}
			}
		}
	}

}
