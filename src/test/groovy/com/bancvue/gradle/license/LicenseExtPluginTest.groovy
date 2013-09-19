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
package com.bancvue.gradle.license

import com.bancvue.gradle.test.AbstractPluginTest
import com.bancvue.gradle.ResourceResolver
import com.bancvue.gradle.test.TestFile
import nl.javadude.gradle.plugins.license.License
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.mockito.Mockito.when


@RunWith(MockitoJUnitRunner)
class LicenseExtPluginTest extends AbstractPluginTest {

	@Mock
	private ResourceResolver resourceResolver
	private LicenseExtPlugin plugin
	private LicenseExtProperties licenseProperties

	LicenseExtPluginTest() {
		super(LicenseExtPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		plugin = new LicenseExtPlugin()
		plugin.init(project)
		licenseProperties = plugin.licenseProperties
		licenseProperties.resourceResolver = resourceResolver
	}

	@Test
	void writeDefaultHeaderFile_ShouldOverwriteAnyExistingContent() {
		TestFile headerFile = new TestFile(plugin.getHeaderFile())
		headerFile << "existing content"
		when(resourceResolver.resolveObjectFromMap(licenseProperties.resourcePath, LicenseModel)).thenReturn(new LicenseModel(header: "new content"))

		plugin.writeLicenseHeaderToBuildDir()

		assert headerFile.text =~ /new content/
		assert !(headerFile.text =~ /existing content/)
	}

	@Test
	void apply_ShouldExcludeDefinedExtensionsFromFormat() {
		project.ext["licenseExcludedFileExtensions"] = ["properties", "yml"]

		project.apply(plugin: "java")
		applyPlugin()

		assert project.tasks.withType(License)
		project.tasks.withType(License) { License task ->
			assert task.excludes == ["**/*.properties", "**/*.yml"] as Set
		}
	}

}
