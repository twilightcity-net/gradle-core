package com.bancvue.gradle.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Extended by tests which are not testing a plugin itself, but testing classes which are used by plugins and
 * require a project.
 */
class AbstractPluginSupportTest {

	@Rule
	public TemporaryFolder projectDir = new TemporaryFolder()
	protected Project project
	protected ProjectFileSystem projectFS

	@Before
	void setUpProject() {
		project = createProject()
		projectFS = new ProjectFileSystem(project.rootDir)
	}

	protected Project createProject() {
		ProjectBuilder.builder()
			.withName("plugin-support")
			.withProjectDir(projectDir.root)
			.build()
	}

}
