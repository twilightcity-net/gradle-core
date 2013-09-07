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
package com.bancvue.gradle

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import org.junit.Test

import static org.junit.Assert.fail

class IdeExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void idea_ShouldAddStandardSourceDirectoriesAndAdditionalTestConfigurations_IfIdePluginDeclaredBeforeTestPlugin() {
		projectFS.buildFile() << """
apply plugin: 'groovy'
apply plugin: 'ide-ext'
apply plugin: 'integration-test'
        """
		projectFS.mkdir("src/main/java")
		projectFS.mkdir("src/test/groovy")
		projectFS.mkdir("src/integrationTest/groovy")

		run("idea")

		File expectedImlFile = projectFS.file("${projectFS.name}.iml")
		assert expectedImlFile.exists()
		assertIdeaModuleFileContainsExpectedSourceFolder(expectedImlFile, "src/main/java", false)
		assertIdeaModuleFileContainsExpectedSourceFolder(expectedImlFile, "src/test/groovy", true)
		assertIdeaModuleFileContainsExpectedSourceFolder(expectedImlFile, "src/integrationTest/groovy", true)
		try {
			assertIdeaModuleFileContainsExpectedSourceFolder(expectedImlFile, "src/someOtherTest/groovy", true)
			fail("Test should have failed, something is likely wrong with positive validations")
		} catch (Throwable ex) {}
	}

	private void assertIdeaModuleFileContainsExpectedSourceFolder(File expectedImlFile, String folderName, boolean isTestFolder) {
		String expectedUrl = "file://\$MODULE_DIR\$/${folderName}"
		def module = new XmlParser().parseText(expectedImlFile.text)

		List result = module.component.content.sourceFolder.findAll {
			it.@url == expectedUrl
		}

		if (!result) {
			fail("Expected sourceFolder url=${expectedUrl} not found in iml content=${expectedImlFile.text}")
		}
		assert result.size() == 1
		assert Boolean.parseBoolean(result[0].@isTestSource) == isTestFolder
	}

	@Test
	void eclipse_ShouldAddStandardSourceDirectoriesAndAdditionalTestConfigurations_IfIdePluginDeclaredAfterTestPlugin() {
		projectFS.buildFile() << """
apply plugin: 'groovy'
apply plugin: 'component-test'
apply plugin: 'ide-ext'
        """
		projectFS.mkdir("src/main/java")
		projectFS.mkdir("src/test/groovy")
		projectFS.mkdir("src/componentTest/groovy")

		run("eclipse")

		File expectedClasspathFile = projectFS.file(".classpath")
		assert expectedClasspathFile.exists()
		assertEclipseModuleFileContainsExpectedSourceFolder(expectedClasspathFile, "src/main/java")
		assertEclipseModuleFileContainsExpectedSourceFolder(expectedClasspathFile, "src/test/groovy")
		assertEclipseModuleFileContainsExpectedSourceFolder(expectedClasspathFile, "src/componentTest/groovy")
		try {
			assertEclipseModuleFileContainsExpectedSourceFolder(expectedClasspathFile, "src/someOtherTest/groovy")
			fail("Test should have failed, something is likely wrong with positive validations")
		} catch (Throwable ex) {}
	}

	private void assertEclipseModuleFileContainsExpectedSourceFolder(File expectedClasspathFile, String folderName) {
		def classpath = new XmlParser().parseText(expectedClasspathFile.text)
		List result = classpath.classpathentry.findAll {
			it.@kind == "src" && it.@path == folderName
		}

		if (!result) {
			fail("Expected classpathentry path=${folderName} not found in .classpath content=${expectedClasspathFile.text}")
		}
	}

}