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

import com.google.common.io.Files
import spock.lang.Specification

class ClearArtifactCacheSpecification extends Specification {

	private File tempDir

	void setup() {
		tempDir = Files.createTempDir()
	}

	def "collectGradleCacheArtifactDirs"() {
		given:
		createDirs(tempDir, ["caches/artifacts-24/filestore", "caches/artifacts-26/module-metadata", "caches/filestore"])

		expect:
		ClearArtifactCache.collectGradleCacheArtifactDirs(tempDir) == [
			new File(tempDir, "caches/artifacts-24/filestore"),
			new File(tempDir, "caches/artifacts-26/module-metadata"),
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
