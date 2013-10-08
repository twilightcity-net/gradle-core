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
package com.bancvue.gradle.categories

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar


class ProjectCategory {

	static String getArtifactId(Project self) {
		self.hasProperty('artifactId') ? self.ext.artifactId : null
	}

	static JavaPluginConvention getJavaConvention(Project self) {
		self.getConvention().getPlugins().get("java") as JavaPluginConvention
	}

	static Jar createJarTask(Project self, String taskName, String sourceSetName, String classifierString = null) {
		String jarContent = classifierString || "classes"
		Jar jarTask = self.tasks.create(taskName, Jar)
		jarTask.configure {
			group = "Build"
			description = "Assembles a jar archive containing the ${sourceSetName} ${jarContent}."
			if (classifierString) {
				classifier = classifierString
			}
		}
		jarTask
	}

}
