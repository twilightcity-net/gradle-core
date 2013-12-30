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

import com.bancvue.gradle.resource.ClasspathUrlResolver
import com.bancvue.gradle.resource.UrlResolver
import com.bancvue.gradle.test.AbstractPluginSupportSpecification
import com.bancvue.gradle.test.TestFile

class ResourceResolverSpecification extends AbstractPluginSupportSpecification {

	private static final class Model {
		String key
	}


	private UrlResolver urlResolver1 = Mock()
	private UrlResolver urlResolver2 = Mock()
	private UrlResolver urlResolver3 = Mock()

	def "getResourceURL should return first resolved url in chain"() {
		given:
		ResourceResolver resolver = new ResourceResolver([urlResolver1, urlResolver2])
		urlResolver2.getResourceAsUrlOrNull(_ as String) >> new URL("file://file1")
		urlResolver3.getResourceAsUrlOrNull(_ as String) >> new URL("file://file2")

		when:
		URL resourceUrl = resolver.getResourceURL("some-path")

		then:
		resourceUrl.toString() == "file://file1"
	}

	def "resolveObjectFromMap should create instance of input type and initialize with map from resource file"() {
		given:
		TestFile mapFile = projectFS.file("map_file")
		mapFile << """
key: "value"
"""
		urlResolver1.getResourceAsUrlOrNull(_ as String) >> mapFile.toURL()
		ResourceResolver resolver = new ResourceResolver([urlResolver1])

		when:
		Model model = resolver.resolveObjectFromMap("map_file", Model)

		then:
		model.key == "value"
	}

	/**
	 * Having the classpath resolver last enables a default version of a resource to be
	 * embedded in the plugin jar but overridden at the project level if desired.
	 */
	def "create should construct resolver which resolves from classpath last"() {
		given:
		ResourceResolver resolver = ResourceResolver.create(project)

		expect:
		resolver.resolverChain.last() instanceof ClasspathUrlResolver
	}

}
