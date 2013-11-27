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

import com.bancvue.gradle.categories.ProjectCategory
import org.gradle.api.Project

class ExtendedPublicationNameResolver {

	private String publicationId

	ExtendedPublicationNameResolver(String publicationId) {
		this.publicationId = publicationId
	}

	String getPublicationIdAppendix() {
		String prefix = publicationId
		if (prefix.startsWith("main")) {
			prefix = publicationId.replaceFirst("main", "")
			if (prefix.size() > 0) {
				prefix = prefix.replaceFirst(prefix[0], prefix[0].toLowerCase())
			}
		}
		prefix
	}

	private String getArtifactIdAppendix() {
		String artifactIdAppendix = publicationIdAppendix
		artifactIdAppendix.replaceAll(/[A-Z]/) { '-' + it }.toLowerCase()
	}

	String getArtifactId(Project project) {
		String artifactId = ProjectCategory.getArtifactId(project)
		String artifactIdAppendix = artifactIdAppendix

		if (artifactIdAppendix) {
			artifactId += "-${artifactIdAppendix}"
		}
		artifactId
	}

	String getJarTaskName() {
		createJarTaskName("jarTask")
	}

	String getSourcesJarTaskName() {
		createJarTaskName("sourcesJarTask")
	}

	private String createJarTaskName(String jarBaseName) {
		String jarTaskNamePrefix = getPublicationIdAppendix()
		!jarTaskNamePrefix ? jarBaseName : jarTaskNamePrefix + jarBaseName.capitalize()
	}

	String getRuntimeConfigurationName() {
		String configurationName = publicationId
		if (configurationName == "main") {
			configurationName = "runtime"
		} else {
			configurationName += "Runtime"
		}
		configurationName
	}

	String getSourceSetName() {
		publicationId
	}

}