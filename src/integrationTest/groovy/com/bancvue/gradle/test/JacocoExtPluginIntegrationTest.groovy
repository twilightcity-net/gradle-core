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

import org.junit.Before
import org.junit.Test

class JacocoExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	private TestFile mainSrcFile
	private TestFile mainTestSrcFile
	private TestFile testDir
	private TestFile componentTestDir

	@Before
	void setUp() {
		mainSrcFile = file("src/main/java/MainClass.java")
		mainTestSrcFile = file("src/mainTest/java/MainTestClass.java")
		testDir = file("src/test/groovy")
		componentTestDir = file("src/componentTest/groovy")

		buildFile << """
apply plugin: 'groovy'

repositories {
	mavenCentral()
}

dependencies {
    testCompile localGroovy()
    testCompile 'junit:junit:4.11'
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
public class ${srcName} {
	public int twoPlusTwo() { return 2 + 2; }
}
""")

		testDir.file("${testName}.groovy").write("""
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

	@Test
	void jacocoReport_ShouldCreateCombinedReportForAllKnownTestConfigurations() {
		createSrcAndTestFiles(mainSrcFile, testDir)
		createSrcAndTestFiles(mainSrcFile, componentTestDir)
		buildFile << """
// NOTE: jacoco-ext is intentionally applied first to verify behavior when the build is modified after the plugin is applied
apply plugin: 'jacoco-ext'
apply plugin: 'component-test'
"""

		run("coverage")

		assert file("build/jacoco/test.exec").size() > 0
		// verify coverage of an externally added test configuration
		assert file("build/jacoco/componentTest.exec").size() > 0
		assert file("build/jacoco/jacocoAllMerge.exec").size() > 0
		// verify coverage was generated for all test configurations
		assert file("build/reports/jacoco/test/html/index.html").exists()
		assert file("build/reports/jacoco/componentTest/html/index.html").exists()
		assert file("build/reports/jacoco/all/html/index.html").exists()
	}

	@Test
	void shouldIncludeAllMainSourceSetsByDefault_AndAlsoRespectOverridesWhenConfigured() {
		createSrcAndTestFiles(mainSrcFile, testDir)
		createSrcAndTestFiles(mainTestSrcFile, componentTestDir)
		buildFile << """
apply plugin: 'jacoco-ext'
apply plugin: 'test-ext'
apply plugin: 'component-test'

jacocoComponentTestReport {
	sourceSets project.sourceSets.main
}
"""
		enableXmlReports()

		run("coverage")

		TestFile testCoverageReport = file("build/reports/jacoco/test/test.xml")
		TestFile componentTestCoverageReport = file("build/reports/jacoco/componentTest/componentTest.xml")
		TestFile allCoverageReport = file("build/reports/jacoco/all/all.xml")
		assertCoverageReportReferencesTestFile(testCoverageReport, mainSrcFile)
		assertCoverageReportReferencesTestFile(testCoverageReport, mainTestSrcFile)
		assertCoverageReportReferencesTestFile(allCoverageReport, mainSrcFile)
		assertCoverageReportReferencesTestFile(allCoverageReport, mainTestSrcFile)
		assertCoverageReportReferencesTestFile(componentTestCoverageReport, mainSrcFile)
		assertCoverageReportDoesNotReferenceTestFile(componentTestCoverageReport, mainTestSrcFile)
	}

}
