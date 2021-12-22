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
package net.twilightcity.gradle.support

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Dependency

class ClearArtifactCache extends DefaultTask {

	String groupName
	File userHome = getDefaultUserHome()
	boolean restrictToProjectDependencies = false
	DependencyResolver dependencyResolver

	public ClearArtifactCache() {
		dependencyResolver = new DependencyResolver()
	}

	private static File getDefaultUserHome() {
		String userHome = System.getProperty("user.home")
		new File(userHome)
	}

	void setGroupName(String groupName) {
		this.groupName = groupName
		this.description = "Deletes any artifacts from the ${groupName} group in the Maven2 repository and Gradle cache directories."
	}

	@TaskAction
	void clearArtifactCache() {
		assertGroupNameSet()
		clearMavenCache()
		clearGradleCache()
		clearGroovyGrapeCache()
	}

	private void assertGroupNameSet() {
		if (!groupName) {
			throw new GradleException("Required property 'groupName' not set")
		}
	}

	private void clearMavenCache() {
		File groupPath = new File(userHome, ".m2/repository/${groupName.replaceAll(/\./, '/')}")

		deleteCachedArtifacts([groupPath])
	}

	private void deleteCachedArtifacts(List<File> cacheDirs) {
		if (restrictToProjectDependencies) {
			for (File cacheDir : cacheDirs) {
				List<String> dependencies = dependencyResolver.getDependenciesForGroup(project, groupName)

				dependencies.each { String name ->
					project.delete new File(cacheDir, name)
				}
			}
		} else {
			project.delete(cacheDirs.toArray())
		}
	}

	private void clearGradleCache() {
		File gradleUserHomeDir = project.gradle.gradleUserHomeDir
		List<File> cacheDirs = collectGradleCacheDirsWithName(gradleUserHomeDir, groupName)

		deleteCachedArtifacts(cacheDirs)
	}

	private static List<File> collectGradleCacheDirsWithName(File gradleUserHomeDir, String groupName) {
		List<File> dirs = []

		collectGradleCacheArtifactDirs(gradleUserHomeDir).each { File filestoreDir ->
			filestoreDir.eachDirMatch( { it == groupName } ) { File dir ->
				dirs << dir
			}
		}
		dirs
	}

	/*
	NOTE: we aren't using eachDirRecurse here because the larger the cache, the longer it takes to clear it when we 
	recurse through all the subdirectories
	*/
	private static List<File> collectGradleCacheArtifactDirs(File gradleUserHomeDir) {
		List<File> dirs = []

		File cachesDir = new File(gradleUserHomeDir, "caches")
		if (cachesDir.exists()) {
			cachesDir.eachDir { File dir ->
				appendCacheDirsFromArtifactsDir(dir, dirs)
				appendCacheDirsFromModulesDir(dir, dirs)
			}
		}
		dirs
	}

	private static void appendCacheDirsFromArtifactsDir(File dir, List<File> dirs) {
		if (dir.name =~ /^artifacts-.*/) {
			appendToListIfDirExists(dirs, new File(dir, "filestore"))
			appendToListIfDirExists(dirs, new File(dir, "module-metadata"))
		}
	}

	private static void appendToListIfDirExists(List<File> fileList, File dir) {
		if (dir.exists()) {
			fileList << dir
		}
	}

	private static void appendCacheDirsFromModulesDir(File dir, List<File> dirs) {
		if (dir.name =~ /^modules-.*/) {
			dir.eachDirMatch(~/^files-.*/) { File filesDir ->
				dirs << filesDir
			}
			dir.eachDirMatch(~/^metadata-.*/) { File metadataDir ->
				appendToListIfDirExists(dirs, new File(metadataDir, "descriptors"))
			}
		}
	}

	private void clearGroovyGrapeCache() {
		File groupDir = new File(userHome, ".groovy/grapes/${groupName}")
		deleteCachedArtifacts([groupDir])
	}



	public static class DependencyResolver {

		List<String> getDependenciesForGroup(Project project, String groupName) {
			List<String> dependencies = []

			project.configurations.each { conf ->
				conf.allDependencies.each { Dependency dep ->
					if (dep.group == groupName) {
						dependencies.add(dep.name)
					}
				}
			}
			dependencies.unique()
		}

	}

}
