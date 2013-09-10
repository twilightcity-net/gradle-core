package com.bancvue.gradle.test

import org.gradle.testkit.functional.ExecutionResult
import org.junit.Test


class ComponentTestPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void sourceSetModificationsShouldBeAppliedToTestClasspath() {
		projectFS.file("resource-dir/resource.txt") << "resource content"
		projectFS.file("src/componentTest/groovy/SomeTest.groovy") << """
import org.junit.Test
class SomeTest {
	@Test
	void test() {
		URL resource =  getClass().getClassLoader().getResource("resource.txt")
		assert resource
		println "Located resource " + resource.file
	}
}
"""
		projectFS.file("build.gradle") << """
apply plugin: 'groovy'
apply plugin: 'component-test'

repositories {
	mavenCentral()
}

dependencies {
    testCompile localGroovy()
    testCompile 'junit:junit:4.11'
}

sourceSets {
	componentTest {
		runtimeClasspath += files("resource-dir")
	}
}

componentTest.testLogging.showStandardStreams = true
"""

		ExecutionResult result = run("check")
		assert result.standardOutput =~ /Located resource .*resource.txt/

	}

}
