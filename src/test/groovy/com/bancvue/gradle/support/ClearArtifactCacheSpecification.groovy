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
package com.bancvue.gradle.support

import com.bancvue.exception.ExceptionSupport
import com.bancvue.gradle.test.AbstractProjectSpecification
import com.google.common.io.Files
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskExecutionException

@Mixin(ExceptionSupport)
class ClearArtifactCacheSpecification extends AbstractProjectSpecification {

	private File tempDir
	private ClearArtifactCache clearArtifactCacheTask

	@Override
	String getProjectName() {
		return 'clearArtifactCache'
	}

	void setup() {
		tempDir = Files.createTempDir()
		clearArtifactCacheTask = project.tasks.create("clearArtifactCacheTask", ClearArtifactCache)
	}

	def "clearArtifactCache should not try to clear cache if group name is not provided"() {
		when:
		clearArtifactCacheTask.execute()

		then:
		TaskExecutionException exception = thrown()
		GradleException cause = getRootCause(exception)
		cause.getMessage() == "Required property 'groupName' not set"
	}

	def "collectGradleCacheArtifactDirs should return filestore and module-metadata when using gradle version <= 1.8"() {
		given:
		createDirs(tempDir, ["caches/artifacts-24/filestore", "caches/artifacts-26/module-metadata", "caches/filestore"])

		expect:
		ClearArtifactCache.collectGradleCacheArtifactDirs(tempDir).sort() == [
			new File(tempDir, "caches/artifacts-24/filestore"),
			new File(tempDir, "caches/artifacts-26/module-metadata"),
		]
	}

	def "collectGradleCacheArtifactDirs should return files and metadata dirs when using gradle version >= 1.9"() {
		given:
		createDirs(tempDir, ["caches/modules-2/files-2.1", "caches/modules-2/metadata-2.2/descriptors"])

		expect:
		ClearArtifactCache.collectGradleCacheArtifactDirs(tempDir).sort() == [
			new File(tempDir, "caches/modules-2/files-2.1"),
			new File(tempDir, "caches/modules-2/metadata-2.2/descriptors")
		]
	}

	def "collectGradleCacheArtifactDirs should not return artifact directory if filestore or module metadata not present"() {
		given:
		createDirs(tempDir, ["caches/artifacts-24"])

		expect:
		ClearArtifactCache.collectGradleCacheArtifactDirs(tempDir) == []
	}

	def "collectGradleCacheDirsWithName"() {
		given:
		File cacheDir = new File(tempDir, "caches/artifacts-24/filestore")
		createDirs(cacheDir, ["com.bancvue", "nomatch", "com.bancvue.nomatch"])

		expect:
		ClearArtifactCache.collectGradleCacheDirsWithName(tempDir, "com.bancvue") == [new File(cacheDir, "com.bancvue")]
	}

	private void createDirs(File parent, List<String> dirNames) {
		dirNames.each { String dirName ->
			new File(parent, dirName).mkdirs()
		}
	}

}
