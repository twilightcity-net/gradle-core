/*
 * Copyright 2014 BancVue, LTD
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
package net.twilightcity.gradle.support


import org.gradle.api.Project

class TaskAndConfigurationNamer {

	private String sourceSetName

	TaskAndConfigurationNamer(String sourceSetName) {
		this.sourceSetName = sourceSetName
	}

	String getSourceSetName() {
		sourceSetName
	}

	String getSourceSetNameAppendix() {
		String prefix = sourceSetName
		if (prefix.startsWith("main")) {
			prefix = sourceSetName.replaceFirst("main", "")
			if (prefix.size() > 0) {
				prefix = prefix.replaceFirst(prefix[0], prefix[0].toLowerCase())
			}
		}
		prefix
	}

	private String getArtifactIdAppendix() {
		String artifactIdAppendix = sourceSetNameAppendix
		artifactIdAppendix.replaceAll(/[A-Z]/) { '-' + it }.toLowerCase()
	}

	String getArtifactId(Project project) {
		String artifactId = net.twilightcity.gradle.categories.ProjectCategory.getArtifactId(project)
		String artifactIdAppendix = artifactIdAppendix

		if (artifactIdAppendix) {
			artifactId += "-${artifactIdAppendix}"
		}
		artifactId
	}

	String getJarTaskName() {
		createTaskName("jar")
	}

	String getSourcesJarTaskName() {
		createTaskName("sourcesJar")
	}

	String getJavadocTaskName() {
		createTaskName("javadoc")
	}

	String getJavadocJarTaskName() {
		createTaskName("javadocJar")
	}

	private String createTaskName(String jarBaseName) {
		String jarTaskNamePostfix = sourceSetName
		if (jarTaskNamePostfix == "main") {
			jarTaskNamePostfix = ""
		}
		jarBaseName + jarTaskNamePostfix.capitalize()
	}

	String getRuntimeConfigurationName() {
		String configurationName = sourceSetName
		if (configurationName == "main") {
			configurationName = "runtime"
		} else {
			configurationName += "Runtime"
		}
		configurationName
	}

	String getCompileConfigurationName() {
		String configurationName = sourceSetName
		if (configurationName == "main") {
			configurationName = "compile"
		} else {
			configurationName += "Compile"
		}
		configurationName
	}

}
