/*
 * Copyright 2014 BancVue, LTD
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
package com.bancvue.gradle.pmd
import com.bancvue.exception.ExceptionSupport
import org.gradle.tooling.BuildException

@Mixin(ExceptionSupport)
class CpdPluginMultiProjectIntegrationSpecification extends AbstractCpdPluginIntegrationSpecification {

	private int minTokenCount = 10

	void setup() {
		file("settings.gradle") << "include 'module'"
		buildFile << """
allprojects {
	apply plugin: 'java'

	repositories {
		mavenCentral()
	}
}
"""
	}

	def "should fail if any project contains cpd violation"() {
		given:
		emptyClassFile("src/main/java/bv/SomeClass.java")
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", minTokenCount)
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

allprojects {
	apply plugin: 'cpd'
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

	def "should not fail if a project contains cpd violation but cpd plugin is not applied"() {
		given:
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}
"""

		and:
		emptyClassFile("src/main/java/bv/SomeClass.java")
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", minTokenCount)

		when:
		run("check")

		then:
		notThrown(BuildException)
	}

	def "should fail if duplicate token threshold exceeded in files across projects"() {
		given:
		int halfMinTokenCount = (minTokenCount / 2) as int
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

allprojects {
	apply plugin: 'cpd'
}
"""

		and:
		classFileWithDuplicateTokens("src/main/java/bv/SomeClass.java", halfMinTokenCount)

		when:
		run("check")

		then:
		notThrown(Exception)

		when:
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", halfMinTokenCount)
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

	def "should include files from sub-project sourceSets not yet created when plugin is applied"() {
		given:
		classFileWithDuplicateTokens("module/src/mainTest/java/bv/ModuleClass.java", minTokenCount)
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

subprojects {
	apply plugin: 'cpd'
	apply plugin: 'test-ext' // creates the 'mainTest' source set
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

}
