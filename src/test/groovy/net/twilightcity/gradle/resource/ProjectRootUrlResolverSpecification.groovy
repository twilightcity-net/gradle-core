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
package net.twilightcity.gradle.resource

class ProjectRootUrlResolverSpecification extends net.twilightcity.gradle.test.AbstractPluginSupportSpecification {

	private ProjectRootUrlResolver resolver

	void setup() {
		resolver = new ProjectRootUrlResolver(project)
	}

	def "getResourceAsUrlOrNull should return URL if resource accessible from project root"() {
		given:
		net.twilightcity.gradle.test.TestFile resourceFile = projectFS.file("somedir/resource.txt") << "content"

		when:
		URL resourceUrl = resolver.getResourceAsUrlOrNull("somedir/resource.txt")

		then:
		resourceUrl == resourceFile.toURL()
	}

	def "getResourceAsUrlOrNull should return null if resource does not exist in project root"() {
		expect:
		resolver.getResourceAsUrlOrNull("does-not-exist.txt") == null
	}

}
