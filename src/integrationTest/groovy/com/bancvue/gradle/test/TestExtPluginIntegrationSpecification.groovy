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

import com.bancvue.zip.ZipArchive
import org.junit.Test

class TestExtPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	def "jarMainTest should compile mainTest source and create jar from source"() {
		given:
		emptyClassFile("src/mainTest/java/Class.java")
		buildFile << """
apply plugin: 'java'
apply plugin: 'test-ext'

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
apply plugin: 'test-ext'

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

}
