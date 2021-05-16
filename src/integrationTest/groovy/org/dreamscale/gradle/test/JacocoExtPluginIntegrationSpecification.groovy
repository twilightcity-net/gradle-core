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

class JacocoExtPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	private TestFile mainSrcFile
	private TestFile mainTestSrcFile
	private TestFile testDir
	private TestFile componentTestDir

	void setup() {
		mainSrcFile = file("src/main/java/bv/MainClass.java")
		mainTestSrcFile = file("src/mainTest/java/bv/MainTestClass.java")
		testDir = file("src/test/groovy/bv")
		componentTestDir = file("src/componentTest/groovy/bv")

		buildFile << """
apply plugin: 'groovy'
apply plugin: 'org.dreamscale.test-ext'

repositories {
	mavenCentral()
}

dependencies {
    sharedTestCompile localGroovy()
    sharedTestCompile 'junit:junit:4.11'
}
"""
	}

	private void enableXmlReports() {
		buildFile << """
tasks.withType(JacocoReport) { report ->
	report.reports {
		xml.enabled true
	}
}
"""
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

	private void assertCoverageReportReferencesTestFile(TestFile xmlCoverageReport, TestFile expectedTestFile) {
		assert xmlCoverageReport.exists()
		assert xmlCoverageReport.text =~ /${expectedTestFile.baseName}/
	}

	private void assertCoverageReportDoesNotReferenceTestFile(TestFile xmlCoverageReport, TestFile expectedTestFile) {
		assert xmlCoverageReport.exists()
		assert !(xmlCoverageReport.text =~ /${expectedTestFile.baseName}/)
	}

	def "should create combined report for all known test configurations"() {
		given:
		createSrcAndTestFiles(mainSrcFile, testDir)
		createSrcAndTestFiles(mainSrcFile, componentTestDir)
		buildFile << """
// NOTE: jacoco-ext is intentionally applied first to verify behavior when the build is modified after the plugin is applied
apply plugin: 'org.dreamscale.jacoco-ext'
apply plugin: 'org.dreamscale.component-test'
"""

		when:
		run("coverage")

		then:
		file("build/jacoco/test.exec").size() > 0
		// verify coverage of an externally added test configuration
		file("build/jacoco/componentTest.exec").size() > 0
		file("build/jacoco/jacocoAllMerge.exec").size() > 0
		// verify coverage was generated for all test configurations
		println projectFS.absolutePath
		file("build/reports/jacoco/jacocoTestReport/html/index.html").exists()
		file("build/reports/jacoco/jacocoComponentTestReport/html/index.html").exists()
		file("build/reports/jacoco/jacocoAllReport/html/index.html").exists()
	}

	def "should include all main source sets by default and also respect overrides when configured"() {
		given:
		createSrcAndTestFiles(mainSrcFile, testDir)
		createSrcAndTestFiles(mainTestSrcFile, componentTestDir)
		buildFile << """
apply plugin: 'org.dreamscale.jacoco-ext'
apply plugin: 'org.dreamscale.component-test'

jacocoComponentTestReport {
	sourceSets sourceSets.main
}
"""
		enableXmlReports()

		when:
		run("coverage")

		then:
		println projectFS.absolutePath
		TestFile testCoverageReport = file("build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
		TestFile componentTestCoverageReport = file("build/reports/jacoco/jacocoComponentTestReport/jacocoComponentTestReport.xml")
		TestFile allCoverageReport = file("build/reports/jacoco/jacocoAllReport/jacocoAllReport.xml")
		assertCoverageReportReferencesTestFile(testCoverageReport, mainSrcFile)
		assertCoverageReportReferencesTestFile(testCoverageReport, mainTestSrcFile)
		assertCoverageReportReferencesTestFile(allCoverageReport, mainSrcFile)
		assertCoverageReportReferencesTestFile(allCoverageReport, mainTestSrcFile)
		assertCoverageReportReferencesTestFile(componentTestCoverageReport, mainSrcFile)
		assertCoverageReportDoesNotReferenceTestFile(componentTestCoverageReport, mainTestSrcFile)
	}

	def "should link to source"() {
		given:
		createSrcAndTestFiles(mainSrcFile, testDir)
		buildFile << """
apply plugin: 'org.dreamscale.jacoco-ext'
"""

		when:
		run("coverage")

		then:
		file("build/reports/jacoco/jacocoTestReport/html/bv/${mainSrcFile.baseName}.html").exists()
		file("build/reports/jacoco/jacocoTestReport/html/bv/${mainSrcFile.name}.html").exists()
	}

}
