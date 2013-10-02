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
package com.bancvue.gradle.maven

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import com.bancvue.gradle.test.TestFile
import org.junit.Before
import org.junit.Test

class MavenExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	private TestFile localMavenRepo

	@Before
	void setUp() {
		localMavenRepo = mkdir("build/maven-repo")
		buildFile << """
ext.repositoryReleaseUrl='${localMavenRepo.toURI()}'
ext.repositorySnapshotUrl='${localMavenRepo.toURI()}'
ext.artifactId='artifact'

group = 'group'
version = '1.0'
"""
	}

	@Test
	void shouldPublishArtifactAndSources() {
		emptyClassFile("src/main/java/Class.java")
		buildFile << """
apply plugin: 'maven-ext'
		"""

		run("publishRemote")

		println projectFS.absolutePath
		assert file("build/libs/artifact-1.0.jar").exists()
		assert file("build/libs/artifact-1.0-sources.jar").exists()
		assert localMavenRepo.file("group/artifact/1.0/artifact-1.0.jar").exists()
		assert localMavenRepo.file("group/artifact/1.0/artifact-1.0-sources.jar").exists()
	}

	@Test
	void shouldSupportDeployerCustomizationsInBuildFile() {
		buildFile << """
apply plugin: 'maven-ext'

project.uploadArchives {
	repositories.mavenDeployer {
		pom.project {
			reporting {
				outputDirectory = "build/reporting"
			}
		}
	}
}
"""

		run("publishRemote")

		TestFile pomFile = file("build/poms/pom-default.xml")

		assert pomFile.text =~ /build\/reporting/
	}

}
