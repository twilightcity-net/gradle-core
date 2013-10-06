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
package com.bancvue.gradle.tasks

import com.google.common.io.Files
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class ClearArtifactCacheTest {

	private ClearArtifactCache task
	private File tempDir

	@Before
	void setUp() {
		Project project = ProjectBuilder.builder().build()
		task = project.tasks.create("clearArtifactCache", ClearArtifactCache)
		tempDir = Files.createTempDir()
	}

	@Test
	void collectGradleCacheArtifactDirs() {
		createDirs(tempDir, ["caches/artifacts-24/filestore", "caches/artifacts-26/module-metadata", "caches/filestore"])

		List<File> dirsWithName = task.collectGradleCacheArtifactDirs(tempDir)

		assert dirsWithName == [
				new File(tempDir, "caches/artifacts-24/filestore"),
				new File(tempDir, "caches/artifacts-26/module-metadata"),
		]
	}

	@Test
	void collectGradleCacheArtifactDirs_ShouldNotReturnArtifactDirectoryIfFilestoreOrModuleMetadataNotPresent() {
		createDirs(tempDir, ["caches/artifacts-24"])

		List<File> dirsWithName = task.collectGradleCacheArtifactDirs(tempDir)

		assert dirsWithName == []
	}

	@Test
	void collectGradleCacheDirsWithName() {
		File cacheDir = new File(tempDir, "caches/artifacts-24/filestore")
		createDirs(cacheDir, ["com.bancvue", "nomatch", "com.bancvue.nomatch"])

		List<File> dirsWithName = task.collectGradleCacheDirsWithName(tempDir, "com.bancvue")

		assert dirsWithName == [new File(cacheDir, "com.bancvue")]
	}

	private void createDirs(File parent, List<String> dirNames) {
		dirNames.each { String dirName ->
			new File(parent, dirName).mkdirs()
		}
	}

}
