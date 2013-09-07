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
package com.bancvue.gradle.custom

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import com.bancvue.gradle.test.TestFile
import com.bancvue.zip.ZipArchive
import org.junit.Test

class CustomGradlePluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void buildCustomGradleDistro_ShouldBundleGradleCustomizationScript() {
		TestFile zipBaseDir = projectFS.mkdir("zipBase")
		zipBaseDir.file('emptyfile.txt') << ""
		projectFS.buildFile() << """
ext {
    repositoryName = 'repo'
    repositoryPublicUrl = 'http://repo.domain/public'
    repositorySnapshotUrl = 'http://repo.domain/snapshots'
    repositoryReleaseUrl = 'http://repo.domain/releases'

    customGradleBaseVersion = "1.7"
    customGradleVersion = "1.7-bv.1.0"
    customGradleGroupName = "com.bancvue"
    customGradleArtifactId = "gradle-bancvue"
}

apply plugin: 'custom-gradle'

// fake out the download and replace it with an empty zip
task createDownloadArchive(type: Zip) {
    from 'zipBase'
	includeEmptyDirs=true
	archiveName=downloadGradle.downloadFileName
	destinationDir=project.buildDir
}
downloadGradle.dependsOn { createDownloadArchive }
downloadGradle.gradleDownloadBase = "file:///\${createDownloadArchive.destinationDir}"

println "Created cutomized gradle dist at \${buildCustomGradleDistro.archivePath}"
        """

		run('buildCustomGradleDistro')

		File expectedZipFile = projectFS.file("build/distributions/gradle-bancvue-1.7-bv.1.0-bin.zip")
		assert expectedZipFile.exists()
		ZipArchive archive = new ZipArchive(expectedZipFile)
		String expectedCustomScript = archive.getContentForEntryWithNameLike('customized.gradle')
		assert expectedCustomScript
		assert expectedCustomScript =~ 'url "http://repo.domain/public"'
		assert expectedCustomScript =~ 'repositoryPublicUrl = "http://repo.domain/public"'
		assert expectedCustomScript =~ 'repositorySnapshotUrl = "http://repo.domain/snapshots"'
		assert expectedCustomScript =~ 'repositoryReleaseUrl = "http://repo.domain/releases"'
	}

}
