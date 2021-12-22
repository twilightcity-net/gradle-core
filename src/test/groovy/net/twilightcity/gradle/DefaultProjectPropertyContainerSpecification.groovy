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
package net.twilightcity.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DefaultProjectPropertyContainerSpecification extends Specification {

	static class TestContainer extends DefaultProjectPropertyContainer {

		String value
		boolean boolValue

		TestContainer(Project project) {
			super(project, "test")
		}
	}


	private Project project
	private TestContainer container

	void setup() {
		project = ProjectBuilder.builder().build()
		container = new TestContainer(project)
	}

	def "propertyAccess should use project default if project default value provided"() {
		when:
		project.ext["testValue"] = "projectDefaultValue"

		then:
		container.value == "projectDefaultValue"
	}

	def "propertyAccess should use project default if project default and property value provided"() {
		when:
		project.ext["testValue"] = "projectDefaultValue"
		container.value = "propertyValue"

		then:
		container.value == "projectDefaultValue"
	}

	def "propertyAccess should use property value if project default not provided"() {
		when:
		container.value = "propertyValue"

		then:
		container.value == "propertyValue"
	}

	def "propertyAccess should respect non string types"() {
		when:
		container.boolValue = true

		then:
		container.boolValue
		container.boolValue.class == Boolean.class
	}

}
