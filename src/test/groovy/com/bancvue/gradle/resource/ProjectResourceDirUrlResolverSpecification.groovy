/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.resource

import com.bancvue.gradle.test.AbstractPluginSupportSpecification
import com.bancvue.gradle.test.TestFile

class ProjectResourceDirUrlResolverSpecification extends AbstractPluginSupportSpecification {

	private ProjectResourceDirUrlResolver resolver

	void setup() {
		resolver = new ProjectResourceDirUrlResolver(project)
		project.apply(plugin: "java")
	}

	def "getResourceAsUrlOrNull should return URL if resource exists in project resource dir"() {
		given:
		TestFile resourceFile = projectFS.file("src/main/resources/resource.txt") << "content"

		when:
		URL resourceUrl = resolver.getResourceAsUrlOrNull("resource.txt")

		then:
		resourceUrl == resourceFile.toURL()
	}

	def "getResourceAsUrlOrNull should return null if resource does not exist in project resource dir"() {
		expect:
		resolver.getResourceAsUrlOrNull("does-not-exist.txt") == null
	}

}
