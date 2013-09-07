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

	@Before
	void setUp() {
		Project project = ProjectBuilder.builder().build()
		task = project.tasks.create('clearArtifactCache', ClearArtifactCache)
	}

	@Test
	void collectGradleCacheDirsWithName() {
		task.groupName = 'com.bancvue'
		File tempDir = Files.createTempDir()
		createDirs(tempDir, ['com.bancvue', 'subdir/com.bancvue', 'nomatch', 'com.bancvue.nomatch'])

		List<File> dirsWithName = task.collectGradleCacheDirsWithName(tempDir, 'com.bancvue')

		assert dirsWithName == [new File(tempDir, 'com.bancvue'), new File(tempDir, 'subdir/com.bancvue')]
	}

	private void createDirs(File parent, List<String> dirNames) {
		dirNames.each { String dirName ->
			new File(parent, dirName).mkdirs()
		}
	}

}
