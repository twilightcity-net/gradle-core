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

import com.bancvue.gradle.test.AbstractPluginSupportTest
import com.bancvue.gradle.test.TestFile
import org.junit.Before
import org.junit.Test

class ResourceResolverTest extends AbstractPluginSupportTest {

	private static final String CLASSPATH_RESOURCE_PATH = "com/bancvue/gradle/ResourceResolverTest.class"

	private ResourceResolver.Impl resolver

	@Before
	void setUp() {
		resolver = new ResourceResolver.Impl(project)
	}

	@Test
	void getNamedResourceAsURLFromProjectResourceDirs_ShouldResolveURL() {
		TestFile resourceFile = projectFS.file("src/main/resources/resource.txt") << "content"
		project.apply(plugin: "java")

		URL resourceUrl = resolver.getNamedResourceAsURLFromProjectResourceDirs("resource.txt")

		assert resourceUrl
		assert resourceUrl == resourceFile.toURL()
	}

	@Test
	void getNamedResourceAsURLFromProjectRoot_ShoudlResolveURL() {
		TestFile resourceFile = projectFS.file("resource.txt") << "content"

		URL resourceUrl = resolver.getNamedResourceAsURLFromProjectRoot("resource.txt")

		assert resourceUrl
		assert resourceUrl == resourceFile.toURL()
	}

	@Test
	void getNamedResourceFromClasspath_ShouldResolveResource() {
		URL resource = resolver.getNamedResourceFromClasspath(CLASSPATH_RESOURCE_PATH)

		assert resource
	}

	@Test
	void acquireResourceURL_ShouldResolveFromResourceDirBeforeProjectRoot() {
		TestFile resourceDirFile = projectFS.file("src/main/resources/resource.txt") << "content"
		TestFile projectRootFile = projectFS.file("resource.txt") << "content"
		project.apply(plugin: "java")

		URL resourceUrl = resolver.acquireResourceURL("resource.txt")

		assert resourceUrl
		assert resourceUrl == resourceDirFile.toURL()
	}

	@Test
	void acquireResourceURL_ShouldResolveResourceFromProjectRootBeforeClasspath() {
		TestFile projectRootFile = projectFS.file(CLASSPATH_RESOURCE_PATH) << "content"
		project.apply(plugin: "java")

		URL resourceUrl = resolver.acquireResourceURL(CLASSPATH_RESOURCE_PATH)

		assert resourceUrl
		assert resourceUrl == projectRootFile.toURL()
	}

}
