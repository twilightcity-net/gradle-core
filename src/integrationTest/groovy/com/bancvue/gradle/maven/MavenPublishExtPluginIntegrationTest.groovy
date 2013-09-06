package com.bancvue.gradle.maven

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import com.bancvue.gradle.test.TestFile
import org.junit.Test


class MavenPublishExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void shouldPublishArtifactAndSources() {
		projectFS.emptyClassFile("src/main/java/Class.java")
		TestFile mavenRepo = projectFS.mkdir("build/maven-repo")
		projectFS.buildFile() << """
ext.repositoryReleaseUrl='${mavenRepo.toURI()}'
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'

group = 'group'
version = '1.0'
		"""

		run("publishRemote")

		assert projectFS.file("build/libs/artifact-1.0.jar").exists()
		assert projectFS.file("build/libs/artifact-1.0-sources.jar").exists()
		assert mavenRepo.file("group/artifact/1.0/artifact-1.0.jar").exists()
		assert mavenRepo.file("group/artifact/1.0/artifact-1.0-sources.jar").exists()
	}

}
