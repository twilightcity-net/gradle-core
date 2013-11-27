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
package com.bancvue.gradle.maven.publish

import com.bancvue.exception.ExceptionSupport
import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import com.bancvue.gradle.test.TestFile
import com.bancvue.zip.ZipArchive
import org.junit.Before
import org.junit.Test


@Mixin(ExceptionSupport)
class MavenPublishExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	private TestFile mavenRepo

	@Before
	void setUp() {
		mavenRepo = mkdir("build/maven-repo")
	}

	private void setupLocalMavenRepoAndApplyPlugin() {
		buildFile << """
ext.repositoryReleaseUrl='${mavenRepo.toURI()}'
ext.artifactId='artifact'

apply plugin: 'maven-publish-ext'
apply plugin: 'project-defaults' // set jar baseName to artifactId

group = 'group'
version = '1.0'
"""
	}

	private String getArchiveName(String artifactId, String classifier = null) {
		"${artifactId}-1.0" + (classifier ? "-${classifier}" : "")
	}

	private TestFile getBuildArtifact(String artifactId, String classifier = null) {
		String jarName = getArchiveName(artifactId, classifier)
		file("build/libs/${jarName}.jar")
	}

	private TestFile getUploadedArtifact(String artifactId, String classifier = null) {
		String jarName = getArchiveName(artifactId, classifier)
		mavenRepo.file("group/${artifactId}/1.0/${jarName}.jar")
	}

	private TestFile getPomFile(String artifactId) {
		mavenRepo.file("group/${artifactId}/1.0/${artifactId}-1.0.pom")
	}

	private ZipArchive assertArchiveBuiltAndUploadedToMavenRepo(String artifactId, String classifier = null) {
		assert getBuildArtifact(artifactId, classifier).exists()
		assert getUploadedArtifact(artifactId, classifier).exists()
		new ZipArchive(getBuildArtifact(artifactId, classifier))
	}

	@Test
	void shouldByDefaultPublishMainArtifactAndSources() {
		emptyClassFile("src/main/java/Class.java")
		setupLocalMavenRepoAndApplyPlugin()

		run("publishRemote")

		ZipArchive archive = assertArchiveBuiltAndUploadedToMavenRepo("artifact")
		assert archive.getEntry("Class.class")
		ZipArchive sourcesArchive = assertArchiveBuiltAndUploadedToMavenRepo("artifact", "sources")
		assert sourcesArchive.getEntry("Class.java")
	}

	@Test
	void shouldSkipPublication_IfEnabledIsFalse() {
		emptyClassFile("src/other/java/MainClass.java")
		buildFile << """
apply plugin: 'maven-publish-ext'

publishing_ext {
	publication("main") {
		enabled false
	}
}
"""

		run("publishRemote")

		assert !getBuildArtifact("artifact").exists()
		assert !getUploadedArtifact("artifact").exists()
	}

	@Test
	void shouldPublishBothMainAndCustomConfiguration_IfCustomConfigurationManuallyConfigured() {
		emptyClassFile("src/main/java/MainClass.java")
		emptyClassFile("src/custom/java/CustomClass.java")
		setupLocalMavenRepoAndApplyPlugin()
		buildFile << """
configurations {
	custom
}

sourceSets {
	custom {
		java {
			srcDir 'src/custom/java'
		}
	}
}

publishing_ext {
	publication("custom")
}
"""

		run("publishRemote")

		assertArchiveBuiltAndUploadedToMavenRepo("artifact")
		assertArchiveBuiltAndUploadedToMavenRepo("artifact", "sources")
		assertArchiveBuiltAndUploadedToMavenRepo("artifact-custom")
		assertArchiveBuiltAndUploadedToMavenRepo("artifact-custom", "sources")
	}

	@Test
	void shouldUseCustomSourceSetAndConfiguration_IfConfigured() {
		emptyClassFile("src/other/java/MainClass.java")
		setupLocalMavenRepoAndApplyPlugin()
		buildFile << """
configurations {
	doesNotMatchConvention
}

sourceSets {
	doesNotMatchConvention {
		java {
			srcDir 'src/other/java'
		}
	}
}

publishing_ext {
	publication("other") {
		sourceSet sourceSets.doesNotMatchConvention
		runtimeConfiguration configurations.doesNotMatchConvention
	}
}
"""

		run("publishRemote")

		assertArchiveBuiltAndUploadedToMavenRepo("artifact-other")
		assertArchiveBuiltAndUploadedToMavenRepo("artifact-other", "sources")
	}

	@Test
	void shouldUseCustomArchiveTasks_IfConfigured() {
		emptyClassFile("src/main/java/MainClass.java")
		setupLocalMavenRepoAndApplyPlugin()
		buildFile << """
task outZip(type: Zip) {
	from sourceSets.main.output
}

task srcZip(type: Zip) {
    classifier = 'sources'
	from sourceSets.main.allSource
}

publishing_ext {
	publication("main") {
		archiveTask outZip
		sourcesArchiveTask srcZip
	}
}
"""

		run("publishRemote")

		String archiveName = getArchiveName("artifact")
		assert mavenRepo.file("group/artifact/1.0/${archiveName}.zip").exists()
		assert mavenRepo.file("group/artifact/1.0/${archiveName}-sources.zip").exists()
	}

	@Test
	void shouldNotPublishSources_IfPublishSourcesSetToFalse() {
		emptyClassFile("src/main/java/MainClass.java")
		setupLocalMavenRepoAndApplyPlugin()
		buildFile << """
task outZip(type: Zip) {
	from sourceSets.main.output
}

publishing_ext {
	publication("main") {
		archiveTask outZip
		publishSources false
	}
}
"""

		run("publishRemote")

		String archiveName = getArchiveName("artifact")
		assert mavenRepo.file("group/artifact/1.0/${archiveName}.zip").exists()
		File sourcesArchive = mavenRepo.file("group/artifact/1.0/").listFiles().find { File file ->
			file.name =~ /sources/
		}
		assert !sourcesArchive
	}

	@Test
	void shouldApplyCompileAndRuntimeDependenciesToMainPom() {
		emptyClassFile("src/main/java/MainClass.java")
		setupLocalMavenRepoAndApplyPlugin()
		buildFile << """
repositories {
	mavenCentral()
}

dependencies {
	compile "org.slf4j:log4j-over-slf4j:1.7.5"
	runtime "ch.qos.logback:logback:0.5"
}
"""

		run("publishRemote")

		TestFile pomFile = getPomFile("artifact")
		assert pomFile.exists()
		// TODO: create abstraction for pom file
		def pom = new XmlParser().parse(pomFile)
		assert pom.dependencies.dependency.size() == 2
		assert pom.dependencies.dependency.find { it.artifactId.text() == "logback" }
		assert pom.dependencies.dependency.find { it.artifactId.text() == "log4j-over-slf4j" }
	}

}
