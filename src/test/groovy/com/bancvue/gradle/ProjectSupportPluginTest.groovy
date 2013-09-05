package com.bancvue.gradle

import com.bancvue.gradle.tasks.ClearArtifactCache
import com.bancvue.gradle.tasks.PrintClasspath
import org.junit.Before
import org.junit.Test

class ProjectSupportPluginTest extends AbstractPluginTest {


	ProjectSupportPluginTest() {
		super(ProjectSupportPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		setArtifactId('1.0')
	}

	@Test
	void apply_ShouldAddPrintClasspathTask() {
		assert project.tasks.withType(PrintClasspath).isEmpty()

		applyPlugin()

		assert !project.tasks.withType(PrintClasspath).isEmpty()
	}

	@Test
	void apply_ShouldAddClearGroupCacheTask() {
		assert project.tasks.findByName('clearGroupCache') == null

		applyPlugin()

		assert project.tasks.findByName('clearGroupCache') instanceof ClearArtifactCache
	}

}
