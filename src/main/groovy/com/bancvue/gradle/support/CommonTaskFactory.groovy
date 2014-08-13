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
package com.bancvue.gradle.support

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar

class CommonTaskFactory {

	private Project project
	private SourceSet sourceSet
	private TaskAndConfigurationNamer namer

	public CommonTaskFactory(Project project, SourceSet sourceSet) {
		this(project, sourceSet, new TaskAndConfigurationNamer(sourceSet.name))
	}

	public CommonTaskFactory(Project project, SourceSet sourceSet, TaskAndConfigurationNamer namer) {
		this.project = project
		this.sourceSet = sourceSet
		this.namer = namer
	}

	Jar createJarTask() {
		createAndConfigureJarTask(namer.jarTaskName, sourceSet.output)
	}

	Jar createSourcesJarTask() {
		createAndConfigureJarTask(namer.sourcesJarTaskName, sourceSet.allSource, "sources")
	}

	private Jar createAndConfigureJarTask(String jarTaskName, Object sourcePath, String classifierString = null) {
		String postfix = namer.sourceSetNameAppendix
		String jarContent = classifierString ? classifierString : "classes"
		Jar jarTask = project.tasks.create(jarTaskName, Jar)
		jarTask.configure {
			group = "Build"
			description = "Assembles a jar archive containing the ${sourceSet.name} ${jarContent}."
			if (classifierString) {
				classifier = classifierString
			}
			if (postfix) {
				baseName = "${baseName}-${postfix}"
			}
			from sourcePath
		}
		jarTask
	}

}
