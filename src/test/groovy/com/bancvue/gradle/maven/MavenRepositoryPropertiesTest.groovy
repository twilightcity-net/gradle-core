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
