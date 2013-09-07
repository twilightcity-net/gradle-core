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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction


class ClearArtifactCache extends DefaultTask {

	{
		group = 'Utilities'
	}

	String groupName

	void setGroupName(String groupName) {
		this.groupName = groupName
		this.description = "Deletes any artifacts from the ${groupName} group in the Maven2 repository and Gradle cache directories."
	}

	@TaskAction
	void clearArtifactCache() {
		assertGroupNameSet()
		clearMavenCache()
		clearGradleCache()
	}

	private void assertGroupNameSet() {
		if (!groupName) {
			throw new GradleException("Required property 'groupName' not set")
		}
	}

	private void clearMavenCache() {
		String cachePath = groupName.replaceAll(/\./, '/')
		project.delete new File(getUserHome(), ".m2/repository/${cachePath}")
	}

	private static File getUserHome() {
		String userHome = System.getProperty('user.home')
		new File(userHome)
	}

	private void clearGradleCache() {
		File gradleUserHomeDir = project.gradle.gradleUserHomeDir
		List<File> cacheDirs = collectGradleCacheDirsWithName(gradleUserHomeDir, groupName)
		project.delete(cacheDirs.toArray())
	}

	private static List<File> collectGradleCacheDirsWithName(File parentDir, String groupName) {
		List<File> dirs = []

		parentDir.eachDirRecurse { File dir ->
			if (dir.name == groupName) {
				dirs << dir
			}
		}
		dirs
	}

}
