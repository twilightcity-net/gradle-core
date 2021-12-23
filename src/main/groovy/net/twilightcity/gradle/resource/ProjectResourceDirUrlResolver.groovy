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
package net.twilightcity.gradle.resource

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

class ProjectResourceDirUrlResolver extends ProjectResourceResolver {

	ProjectResourceDirUrlResolver(Project project) {
		super(project)
	}

	@Override
	URL getResourceAsUrlOrNull(String resourcePath) {
		File headerResourceFile = getNamedResourceAsFileFromProjectResourceDirs(resourcePath)
		toURL(headerResourceFile)
	}

	private File getNamedResourceAsFileFromProjectResourceDirs(String resourcePath) {
		ArrayList<File> srcDirs = collectProjectResourceDirs()

		for (File srcDir : srcDirs) {
			File file = new File(srcDir, resourcePath)
			if (file.exists()) {
				return file
			}
		}
		null
	}

	private List<File> collectProjectResourceDirs() {
		if (project.hasProperty("sourceSets") == false) {
			return Collections.emptyList()
		}

		project.sourceSets.collect { SourceSet sourceSet ->
			sourceSet.resources.srcDirs
		}.flatten()
	}
}
