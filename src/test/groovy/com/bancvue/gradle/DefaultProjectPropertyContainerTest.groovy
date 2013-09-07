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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class DefaultProjectPropertyContainerTest {

	static class TestContainer extends DefaultProjectPropertyContainer {

		String value
		boolean boolValue

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

	@Test
	void propertyAccess_ShouldRespectNonStringTypes() {
		container.boolValue = true

		assert container.boolValue
		assert container.boolValue.class == Boolean.class
	}

}
