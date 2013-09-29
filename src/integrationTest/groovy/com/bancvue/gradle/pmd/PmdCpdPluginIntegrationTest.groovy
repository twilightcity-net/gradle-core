package com.bancvue.gradle.pmd

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import org.gradle.tooling.BuildException
import org.junit.Test

class PmdCpdPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void shouldDetectCpdViolationAndWriteXmlAndHtmlReport() {
		file("src/main/java/Class.java") << """
public class Class {
	public void violate() {
		int x++;
		${"x++; " * 11}
	}
}
"""

		buildFile << """
apply plugin: 'java'
apply plugin: 'pmdcpd'

repositories {
	mavenCentral()
}

cpd {
    minimumTokenCount 10
}
		"""

		try {
			run("check")
		} catch (BuildException ex) {}

		assert file("build/reports/cpd/main.xml").exists()
		assert file("build/reports/cpd/main.html").exists()
	}

}
