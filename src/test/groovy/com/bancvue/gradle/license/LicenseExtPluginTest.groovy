package com.bancvue.gradle.license

import com.bancvue.gradle.AbstractPluginTest
import com.bancvue.gradle.test.TestFile
import nl.javadude.gradle.plugins.license.License
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.when


@RunWith(MockitoJUnitRunner)
class LicenseExtPluginTest extends AbstractPluginTest {

	@Mock
	private HeaderContentResolver headerContentResolver
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
		plugin.headerContentResolver = headerContentResolver
	}

	@Test
	void writeDefaultHeaderFile_ShouldOverwriteAnyExistingContent() {
		licenseProperties.headerResourcePath = "HEADER"
		TestFile headerFile = projectFS.file("build/HEADER") << "existing content"
		when(headerContentResolver.acquireHeaderResourceContent(anyString())).thenReturn("new content")

		plugin.writeDefaultHeaderFile()

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
