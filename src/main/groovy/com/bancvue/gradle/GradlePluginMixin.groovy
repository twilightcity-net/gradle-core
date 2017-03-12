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
package com.bancvue.gradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.slf4j.Logger

class GradlePluginMixin {

	static FilenameFilter JAR_FILTER = { dir, file -> file ==~ /(?i).*\.jar$/ } as FilenameFilter

	Project acquirePluginProject() {
		assertPropertyDefined('project')
		project
	}

	Logger acquirePluginLog() {
		assertPropertyDefined('log')
		log
	}

	void assertPropertyDefined(String propertyName) {
		if (!hasProperty(propertyName)) {
			throw new RuntimeException("Mixin requires property '${propertyName}' on target class")
		}
	}

	ConfigurableFileCollection createJarFileCollection(File dir) {
		File[] files = dir.listFiles(JAR_FILTER)

		if (!files) {
			acquirePluginLog().warn("Failed to create file collection, no jar files in dir=${dir}")
			return null
		}
		acquirePluginProject().files(files)
	}

	void createNamedConfigurationExtendingFrom(String configurationName, String extendsFromConfigurationName) {
		String extendsFromCompileConfigurationName = "${extendsFromConfigurationName}Compile"
		String extendsFromCompileOnlyConfigurationName = "${extendsFromConfigurationName}CompileOnly"
		String extendsFromRuntimeConfigurationName = "${extendsFromConfigurationName}Runtime"
		createNamedConfigurationExtendingFrom(configurationName, extendsFromCompileConfigurationName,
                                              extendsFromCompileOnlyConfigurationName, extendsFromRuntimeConfigurationName)
	}

	void createNamedConfigurationExtendingFrom(String configurationName, String extendsFromCompileConfigurationName,
												String extendsFromCompileOnlyConfigurationName, String extendsFromRuntimeConfigurationName) {
		Configuration extendsFromCompileConfiguration = acquireConfigurationByName(extendsFromCompileConfigurationName)
		Configuration extendsFromCompileOnlyConfiguration = acquireConfigurationByName(extendsFromCompileOnlyConfigurationName)
		Configuration extendsFromRuntimeConfiguration = acquireConfigurationByName(extendsFromRuntimeConfigurationName)

		acquirePluginProject().configurations {
			"${configurationName}" {}
			"${configurationName}Compile" {
				extendsFrom(extendsFromCompileConfiguration)
			}
			"${configurationName}CompileOnly" {
				extendsFrom(extendsFromCompileOnlyConfiguration)
			}
			"${configurationName}Runtime" {
				extendsFrom(extendsFromRuntimeConfiguration)
			}
		}
	}

	Configuration acquireConfigurationByName(String extendsFromCompileConfigurationName) {
		acquirePluginProject().configurations.getByName(extendsFromCompileConfigurationName)
	}

}
