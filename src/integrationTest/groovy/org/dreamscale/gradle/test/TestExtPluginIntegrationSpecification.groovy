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
package org.dreamscale.gradle.test

import org.dreamscale.zip.ZipArchive
import org.gradle.testkit.runner.BuildResult

class TestExtPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	def "jarMainTest should compile mainTest source and create jar from source"() {
		given:
		emptyClassFile("src/mainTest/java/Class.java")
		buildFile << """
apply plugin: 'java'
apply plugin: 'org.dreamscale.test-ext'

jarMainTest.archiveName='mainTest.jar'
        """

		when:
		run("check", "jarMainTest")

		then:
		file("build/classes/mainTest/Class.class").exists()
		ZipArchive mainTestJar = projectFS.archive("build/libs/mainTest.jar")
		mainTestJar.exists()
		mainTestJar.acquireContentForEntryWithNameLike("Class.class")
	}

	def "javadocJarMainTest should generate mainTest javadoc and create jar from javadoc"() {
		given:
		file("src/mainTest/java/Class.java") << """
/**
 * Here are some docs
 */
public class Class {}
"""
		buildFile << """
apply plugin: 'java'
apply plugin: 'org.dreamscale.test-ext'

javadocJarMainTest.archiveName='mainTestJavadoc.jar'
        """

		when:
		run("check", "javadocJarMainTest")

		then:
		file("build/docs/mainTestDocs/Class.html").exists()
		ZipArchive mainTestJavadocJar = projectFS.archive("build/libs/mainTestJavadoc.jar")
		mainTestJavadocJar.exists()
		mainTestJavadocJar.acquireContentForEntryWithNameLike("Class.html")
	}

	def "styledTestOutput should print test progress"() {
		given:
		buildFile << """
apply plugin: 'groovy'
apply plugin: 'org.dreamscale.test-ext'

repositories {
	mavenCentral()
}

dependencies {
	testCompile 'org.spockframework:spock-core:0.7-groovy-2.0'
}
"""
 		file("src/test/groovy/SomeTest.groovy") << """
class SomeTest extends spock.lang.Specification {

	def "styled test output test"() {
		expect:
			true
	}
}
"""
		when:
		BuildResult result = run("test", "styledTestOutput")

		then:
		result.output =~ /styled test output test/
	}

}
