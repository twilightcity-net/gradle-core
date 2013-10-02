/**
 * Copyright 2013 BancVue, LTD
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
