/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle.ide

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet


class ProjectInfo {

	Set<File> sourceDirs
	Set<File> testSourceDirs
	Set<Configuration> compileConfigurations
	Set<Configuration> runtimeConfigurations
	Set<Configuration> testConfigurations

	ProjectInfo(Project project) {
		sourceDirs = []
		testSourceDirs = []
		project.sourceSets.each { SourceSet sourceSet ->
			Set<File> sourceSetSrcDirs = sourceSet.allSource.srcDirs

			if (sourceSet.name =~ /(?i).*test$/) {
				testSourceDirs.addAll(sourceSetSrcDirs)
			} else {
				sourceDirs.addAll(sourceSetSrcDirs)
			}
		}

		compileConfigurations = findConfigurationsMatching(project, /(?i).*(?<!test)compile$/)
		runtimeConfigurations = findConfigurationsMatching(project, /(?i).*(?<!test)runtime$/)
		testConfigurations = findConfigurationsMatching(project, /(?i).*testcompile$/)
	}

	private Set<Configuration> findConfigurationsMatching(Project project, String regex) {
		project.configurations.findAll { Configuration config ->
			config.name =~ regex
		} as Set
	}

}
