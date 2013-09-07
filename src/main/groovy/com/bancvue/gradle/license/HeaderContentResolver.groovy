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
package com.bancvue.gradle.license

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet


interface HeaderContentResolver {

	String acquireHeaderResourceContent(String headerResourcePath)


	static class Impl implements HeaderContentResolver {

		private Project project

		Impl(Project project) {
			this.project = project
		}

		String acquireHeaderResourceContent(String headerResourcePath) {
			def resource = getHeaderFromSourceSetOrPath(headerResourcePath)
			if (resource == null) {
				throw new RuntimeException("Failed to resolve header resource with path=${headerResourcePath}")
			}
			resource.text
		}

		private def getHeaderFromSourceSetOrPath(String resourceName) {
			def resource = getHeaderResourceFromProjectSourceSets(resourceName)
			if (resource == null) {
				resource = getClass().getResource(resourceName)
			}
			resource
		}

		private File getHeaderResourceFromProjectSourceSets(String resourceName) {
			File headerResourceFile = null
			def srcDirs = project.sourceSets.collect { SourceSet sourceSet ->
				sourceSet.resources.srcDirs
			}

			srcDirs*.each { File srcDir ->
				File file = new File(srcDir, resourceName)
				if (!headerResourceFile && file.exists()) {
					headerResourceFile = file
				}
			}
			headerResourceFile
		}
	}
}
