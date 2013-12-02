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

import com.bancvue.gradle.test.AbstractPluginSupportSpecification
import com.bancvue.gradle.test.TestFile
import org.junit.Before
import org.junit.Test

class ResourceResolverSpecification extends AbstractPluginSupportSpecification {

	private static final class Model {
		String key
	}


	private static final String CLASSPATH_RESOURCE_PATH = "com/bancvue/gradle/ResourceResolverSpecification.class"

	private ResourceResolver.Impl resolver

	void setup() {
		resolver = new ResourceResolver.Impl(project)
		project.apply(plugin: "java")
	}

	def "getNamedResourceAsURLFromProjectResourceDirs should resolve URL"() {
		given:
		TestFile resourceFile = projectFS.file("src/main/resources/resource.txt") << "content"

		when:
		URL resourceUrl = resolver.getNamedResourceAsURLFromProjectResourceDirs("resource.txt")

		then:
		resourceUrl == resourceFile.toURL()
	}

	def "getNamedResourceAsURLFromProjectRoot shoudl resolve URL"() {
		given:
		TestFile resourceFile = projectFS.file("resource.txt") << "content"

		when:
		URL resourceUrl = resolver.getNamedResourceAsURLFromProjectRoot("resource.txt")

		then:
		resourceUrl == resourceFile.toURL()
	}

	def "getNamedResourceFromClasspath should resolve resource"() {
		expect:
		resolver.getNamedResourceFromClasspath(CLASSPATH_RESOURCE_PATH)
	}

	def "acquireResourceURL should resolve from resource dir before project root"() {
		given:
		TestFile resourceDirFile = projectFS.file("src/main/resources/resource.txt") << "content"
		projectFS.file("resource.txt") << "content"

		when:
		URL resourceUrl = resolver.acquireResourceURL("resource.txt")

		then:
		resourceUrl == resourceDirFile.toURL()
	}

	def "acquireResourceURL should resolve resource from project root before classpath"() {
		given:
		assert resolver.getNamedResourceFromClasspath(CLASSPATH_RESOURCE_PATH)
		TestFile projectRootFile = projectFS.file(CLASSPATH_RESOURCE_PATH) << "content"

		when:
		URL resourceUrl = resolver.acquireResourceURL(CLASSPATH_RESOURCE_PATH)

		then:
		resourceUrl == projectRootFile.toURL()
	}

	def "resolveObjectFromMap should create instance of input type and initialize with map from resource file"() {
		given:
		projectFS.file("src/main/resources/map_file") << """
key: "value"
"""

		when:
		Model model = resolver.resolveObjectFromMap("map_file", Model)

		then:
		model.key == "value"
	}

}
