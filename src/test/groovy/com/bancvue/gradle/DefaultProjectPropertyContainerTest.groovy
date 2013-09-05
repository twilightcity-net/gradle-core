package com.bancvue.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class DefaultProjectPropertyContainerTest {

	static class TestContainer extends DefaultProjectPropertyContainer {

		String value

		TestContainer(Project project) {
			super(project, "test")
		}
	}


	private Project project
	private TestContainer container

	@Before
	void setUp() {
		project = ProjectBuilder.builder().build()
		container = new TestContainer(project)
	}

	@Test
	void propertyAccess_ShouldUseProjectDefault_IfProjectDefaultValueProvided() {
		project.ext["testValue"] = "projectDefaultValue"

		assert container.value == "projectDefaultValue"
	}

	@Test
	void propertyAccess_ShouldUseProjectDefault_IfProjectDefaultAndPropertyValueProvided() {
		project.ext["testValue"] = "projectDefaultValue"
		container.value = "propertyValue"

		assert container.value == "projectDefaultValue"
	}

	@Test
	void propertyAccess_ShouldUsePropertyValue_IfProjectDefaultNotProvided() {
		container.value = "propertyValue"

		assert container.value == "propertyValue"
	}

}
