package com.bancvue.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder


abstract class AbstractPluginTest {

	@Rule
	public TemporaryFolder projectDir = new TemporaryFolder()
	protected Project project
	protected String pluginName

	AbstractPluginTest(String pluginName) {
		this.pluginName = pluginName
	}

	@Before
	void setUpProject() {
		project = createProject()
	}

	protected Project createProject() {
		ProjectBuilder.builder()
			.withName("${pluginName}-project")
			.withProjectDir(projectDir.root)
			.build()
	}

	protected void applyPlugin() {
		project.apply(plugin: pluginName)
	}

	protected void assertNamedPluginApplied(String pluginName) {
		assert project.plugins.getPlugin(pluginName) != null
	}

	protected void setArtifactId(String artifactId) {
		project.ext['artifactId'] = artifactId
	}

}
