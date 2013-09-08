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
