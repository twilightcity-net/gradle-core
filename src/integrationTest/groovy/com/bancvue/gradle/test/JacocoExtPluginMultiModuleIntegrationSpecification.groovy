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
package com.bancvue.gradle.test

class JacocoExtPluginMultiModuleIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	void setup() {
		buildFile << """
allprojects {
	apply plugin: 'groovy'
	apply plugin: 'jacoco-ext'

	repositories {
		mavenCentral()
	}

	dependencies {
	    testCompile localGroovy()
	    testCompile 'junit:junit:4.11'
	}
}
"""

		TestFile srcFile = file("src/main/java/bv/SomeClass.java")
		TestFile testDir = file("src/test/groovy/bv")
		createSrcAndTestFiles(srcFile, testDir)
		TestFile module1SrcFile = file("module1/src/main/java/bv/Module1Class.java")
		TestFile module1TestDir = file("module1/src/test/groovy/bv")
		createSrcAndTestFiles(module1SrcFile, module1TestDir)
		TestFile module2SrcFile = file("module2/src/main/java/bv/Module2Class.java")
		TestFile module2TestDir = file("module2/src/test/groovy/bv")
		createSrcAndTestFiles(module2SrcFile, module2TestDir)
		file("settings.gradle") << "include 'module1', 'module2'"
	}

	private void createSrcAndTestFiles(TestFile srcFile, TestFile testDir) {
		String srcName = srcFile.baseName
		String testName = "${srcName}Test"

		srcFile.write("""
package bv;

public class ${srcName} {
	public int twoPlusTwo() { return 2 + 2; }
}
""")

		testDir.file("${testName}.groovy").write("""
package bv

import org.junit.Test

public class ${testName} {
	@Test
	void test() {
		assert new ${srcName}().twoPlusTwo() == 4
	}
}
""")
	}

	def "should incorporate submodule reports by default"() {
		when:
		run("coverage")

		then:
		String allResults = file("build/reports/jacoco/all/all.xml").text
		allResults =~ /SomeClass/
		allResults =~ /Module1Class/
		allResults =~ /Module2Class/
	}

	def "should not incorporate submodule reports if includeSubProjectsInAllReport set to false"() {
		given:
		buildFile << """
jacoco_ext {
	includeSubProjectsInAllReport = false
}
"""

		when:
		run("coverage")

		then:
		String allResults = file("build/reports/jacoco/all/all.xml").text
		allResults =~ /SomeClass/
		!(allResults =~ /Module1Class/)
		!(allResults =~ /Module2Class/)
	}

}
