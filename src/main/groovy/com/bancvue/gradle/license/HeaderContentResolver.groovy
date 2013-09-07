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
