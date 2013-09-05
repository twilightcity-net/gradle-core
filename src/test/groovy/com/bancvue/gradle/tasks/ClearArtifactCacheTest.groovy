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
