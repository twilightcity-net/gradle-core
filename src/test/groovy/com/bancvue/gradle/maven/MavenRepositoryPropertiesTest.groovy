package com.bancvue.gradle.maven

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class MavenRepositoryPropertiesTest {

	private Project project
	private MavenRepositoryProperties properties

	@Before
	void setUp() {
		project = ProjectBuilder.builder().build()
		properties = new MavenRepositoryProperties(project)
	}

	@Test
	void hasCredentialsDefined_ShouldReturnFalse_IfUsernameOnlyDefined() {
		properties.username = "username"

		assert !properties.hasCredentialsDefined()
	}

	@Test
	void hasCredentialsDefined_ShouldReturnFalse_IfPasswordOnlyDefined() {
		properties.password = "password"

		assert !properties.hasCredentialsDefined()
	}

	@Test
	void hasCredentialsDefined_ShouldReturnFalse_IfUsernameAndPasswordNotDefined() {
		assert !properties.hasCredentialsDefined()
	}
	
	@Test
	void hasCredentialsDefined_ShouldReturnTrue_IfUsernameAndPasswordDefined() {
		properties.username = "username"
		properties.password = "password"

		assert properties.hasCredentialsDefined()
	}

	@Test
	void hasCredentialsDefined_ShouldReturnTrue_IfUsernameAndPasswordDefinedOnProject() {
		project.ext["repositoryUsername"] = 'username'
		project.ext["repositoryPassword"] = 'password'

		assert properties.hasCredentialsDefined()
	}
}
