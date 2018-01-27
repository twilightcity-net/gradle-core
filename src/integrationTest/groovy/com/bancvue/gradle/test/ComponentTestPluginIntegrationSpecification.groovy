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
package com.bancvue.gradle.test

import org.gradle.testkit.runner.BuildResult

class ComponentTestPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	def "should apply source set modifications to test classpath"() {
		given:
		file("resource-dir/resource.txt") << "resource content"
		file("src/componentTest/groovy/SomeTest.groovy") << """
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
		buildFile << """
apply plugin: 'groovy'
apply plugin: 'org.dreamscale.component-test'

repositories {
	mavenCentral()
}

dependencies {
    sharedTestCompile localGroovy()
    sharedTestCompile 'junit:junit:4.11'
}

sourceSets {
	componentTest {
		runtimeClasspath += files("resource-dir")
	}
}

componentTest.testLogging.showStandardStreams = true
"""

		when:
		BuildResult result = run("check")

		then:
		result.output =~ /Located resource .*resource.txt/
	}

}
