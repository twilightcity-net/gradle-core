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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet


interface ResourceResolver {

	public <T> T resolveObjectFromMap(String resourcePath, Class<T> type)

	URL acquireResourceURL(String resourcePath)

	String acquireResourceContent(String resourcePath)


	static class Impl implements ResourceResolver {

		private Project project

		Impl(Project project) {
			this.project = project
		}

		public <T> T resolveObjectFromMap(String resourcePath, Class<T> type) {
			String jsonContent = acquireResourceContent(resourcePath)
			Map result = Eval.me("[${jsonContent}]")
			type.newInstance(result)
		}

		String acquireResourceContent(String resourcePath) {
			acquireResourceURL(resourcePath).text
		}

		URL acquireResourceURL(String resourcePath) {
			if (resourcePath == null) {
				throw new GradleException("Invalid input, resourcePath must not be null")
			}

			def resource = getResourceFromResourceDirOrProjectDirOrPath(resourcePath)
			if (resource == null) {
				throw new GradleException("Failed to resolve resource with path=${resourcePath}")
			}
			resource
		}

		private URL getResourceFromResourceDirOrProjectDirOrPath(String resourceName) {
			URL resource = getNamedResourceAsURLFromProjectResourceDirs(resourceName)
			if (resource == null) {
				resource = getNamedResourceAsURLFromProjectRoot(resourceName)
			}
			if (resource == null) {
				resource = getNamedResourceFromClasspath(resourceName)
			}
			resource
		}

		private URL getNamedResourceFromClasspath(String resourceName) {
			getClass().getClassLoader().getResource(resourceName)
		}

		private URL getNamedResourceAsURLFromProjectRoot(String resourceName) {
			File file = project.file(resourceName)
			toURL(file)
		}

		private URL getNamedResourceAsURLFromProjectResourceDirs(String resourceName) {
			File headerResourceFile = getNamedResourceAsFileFromProjectResourceDirs(resourceName)
			toURL(headerResourceFile)
		}

		private URL toURL(File file) {
			URL url = null
			if ((file != null) && file.exists()) {
				url = file.toURI().toURL()
			}
			url
		}

		private File getNamedResourceAsFileFromProjectResourceDirs(String resourceName) {
			ArrayList<File> srcDirs = collectProjectResourceDirs()

			for (File srcDir : srcDirs) {
				File file = new File(srcDir, resourceName)
				if (file.exists()) {
					return file
				}
			}
			null
		}

		private List<File> collectProjectResourceDirs() {
			project.sourceSets.collect { SourceSet sourceSet ->
				sourceSet.resources.srcDirs
			}.flatten()
		}
	}
}
