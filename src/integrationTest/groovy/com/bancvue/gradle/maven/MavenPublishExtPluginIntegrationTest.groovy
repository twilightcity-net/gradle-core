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

import com.bancvue.exception.ExceptionSupport
import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import com.bancvue.gradle.test.TestFile
import org.junit.Test


@Mixin(ExceptionSupport)
class MavenPublishExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void shouldPublishArtifactAndSources() {
		emptyClassFile("src/main/java/Class.java")
		TestFile mavenRepo = mkdir("build/maven-repo")
		buildFile << """
ext.repositoryReleaseUrl='${mavenRepo.toURI()}'
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'

group = 'group'
version = '1.0'
		"""

		run("publishRemote")

		assert file("build/libs/artifact-1.0.jar").exists()
		assert file("build/libs/artifact-1.0-sources.jar").exists()
		assert mavenRepo.file("group/artifact/1.0/artifact-1.0.jar").exists()
		assert mavenRepo.file("group/artifact/1.0/artifact-1.0-sources.jar").exists()
	}

	@Test
	void customizeDefaultArtifact() {
		buildFile << """
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'

publishingext {
	publications {
		"artifact" {
			pom.withXml {
				asNode().children().last() + {
					resolveStrategy = Closure.DELEGATE_FIRST
					reporting {
						outputDirectory = file("build/reporting")
					}
				}
			}
		}
	}
}
"""

		run("generatePomFileForArtifactPublication")

		TestFile pomFile = file("build/publications/artifact/pom-default.xml")

		assert pomFile.text =~ /build\/reporting/
	}

	@Test
	void shouldFailBuild_IfExtendedPublicationDefinedButNoMatchingMavenPublicationIsFound() {
		buildFile << """
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'

publishingext {
	publications {
		"otherArtifact" {}
	}
}
"""

		try {
			run("generatePomFileForArtifactPublication")
			assert false : "Expected build failure"
		} catch (Exception ex) {
			assert getRootCause(ex).message == "Extended publication defined with name 'otherArtifact' but no matching publication found"
		}
	}

	@Test
	void shouldFailWithMessageRepresentativeOfError_IfCustomPublicationBlockContainsError() {
		buildFile << """
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'

publishingext {
	publications {
		"artifact" {
			undefinedMethod {}
		}
	}
}
"""

		try {
			run("generatePomFileForArtifactPublication")
			assert false : "Expected build failure"
		} catch (Exception ex) {
			assert getRootCause(ex).message =~ /^Could not find method undefinedMethod.*/
		}
	}

}
