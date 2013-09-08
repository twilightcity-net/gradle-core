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
package com.bancvue.gradle.license

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import org.gradle.testkit.functional.ExecutionResult
import org.junit.Test

class LicenseExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	/**************************************************************************************************************
	 * NOTE: if these test fail in an IDE, it may need to add 'headers/*' to the compiler settings so resources
	 * are copied appropriately
	 **************************************************************************************************************/

	@Test
	void licenseFormat_ShouldWriteLicenseHeaderToSourceFiles() {
		List<File> srcFiles = ["src/main/java", "src/mainTest/java", "src/test/java"].collect{ String path ->
			projectFS.emptyClassFile("${path}/Class.java")
		}
		projectFS.buildFile() << """
ext {
	licenseName='BancVue'
}

apply plugin: 'test-ext'
apply plugin: 'license-ext'
        """

		run("licenseFormat")

		String year = Calendar.getInstance().get(Calendar.YEAR)
		srcFiles.each { File srcFile ->
			String text = srcFile.text
			assert text =~ /Copyright ${year} BancVue/
			assert text =~ /www.apache.org/
		}
	}

	@Test
	void licenseFormat_ShouldNotFailIfBuildIsFirstCleaned() {
		File srcFile = projectFS.emptyClassFile('src/main/java/Class.java')
		projectFS.buildFile() << """
apply plugin: 'java'
apply plugin: 'license-ext'

license {
    ext.year='1975'
	ext.name='BancVue'
}
		"""

		run("clean", "licenseFormat")

		String text = srcFile.text
		assert text =~ /Copyright 1975 BancVue/
		assert text =~ /www.apache.org/
	}

	@Test
	void licenseFormat_ShouldUseAlternativeHeaderIfProvided() {
		File srcFile = projectFS.emptyClassFile('src/main/java/Class.java')
		projectFS.file('src/main/resources/ALT_HEADER') << "ALTERNATIVE HEADER"
		projectFS.buildFile() << """
ext {
	licenseHeaderResourcePath='ALT_HEADER'
}

apply plugin: 'java'
apply plugin: 'license-ext'
        """

		run("assemble", "licenseFormat")

		String text = srcFile.text
		println text
		assert text =~ /ALTERNATIVE HEADER/
	}

	@Test
	void licenseCheck_ShouldCheckLicenseHeaderInSourceFiles() {
		File srcFile = projectFS.emptyClassFile('src/main/java/Class.java')
		projectFS.buildFile() << """
apply plugin: 'java'
apply plugin: 'license-ext'
        """

		ExecutionResult result = run("licenseCheck")

		assert result.standardOutput =~ /Missing header in: .*${srcFile.name}/
	}

}
