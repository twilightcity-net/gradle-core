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
package org.dreamscale.gradle.license

import org.dreamscale.gradle.test.AbstractPluginIntegrationSpecification
import org.gradle.testkit.runner.BuildResult

class LicenseExtPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	/**************************************************************************************************************
	 * NOTE: if these test fail in an IDE, you may need to add 'licenses/*' to the compiler settings so resources
	 * are copied appropriately
	 **************************************************************************************************************/

	def "licenseFormat should write license header to source files"() {
		given:
		List<File> srcFiles = ["src/main/java", "src/mainTest/java", "src/test/java"].collect{ String path ->
			emptyClassFile("${path}/Class.java")
		}
		buildFile << """
ext {
	licenseName='BancVue'
}

apply plugin: 'org.dreamscale.test-ext'
apply plugin: 'org.dreamscale.license-ext'
        """

		when:
		run("licenseFormat")

		then:
		String year = Calendar.getInstance().get(Calendar.YEAR)
		srcFiles.each { File srcFile ->
			String text = srcFile.text
			assert text =~ /Copyright ${year} BancVue/
			assert text =~ /www.apache.org/
		}
	}

	def "licenseFormat should not fail if build is first cleaned"() {
		given:
		File srcFile = emptyClassFile('src/main/java/Class.java')
		buildFile << """
apply plugin: 'java'
apply plugin: 'org.dreamscale.license-ext'

license {
    ext.year='1975'
	ext.name='BancVue'
}
		"""

		when:
		run("clean", "licenseFormat")

		then:
		String text = srcFile.text
		text =~ /Copyright 1975 BancVue/
		text =~ /www.apache.org/
	}

	def "licenseFormat should use alternative header if provided"() {
		given:
		File srcFile = emptyClassFile('src/main/java/Class.java')
		file('src/main/resources/ALT_LICENSE') << 'header: "ALTERNATIVE HEADER"'
		buildFile << """
ext {
	licenseResourcePath='ALT_LICENSE'
}

apply plugin: 'java'
apply plugin: 'org.dreamscale.license-ext'
        """

		when:
		run("assemble", "licenseFormat", "--info")

		then:
		String text = srcFile.text
		text =~ /ALTERNATIVE HEADER/
	}

	def "licenseCheck should check license header in source files"() {
		given:
		File srcFile = emptyClassFile('src/main/java/Class.java')
		buildFile << """
apply plugin: 'java'
apply plugin: 'org.dreamscale.license-ext'

license {
	ignoreFailures true
}
        """

		when:
		BuildResult result = run("licenseCheck")

		then:
		result.output =~ /Missing header in: .*${srcFile.name}/
	}

	def "licenseCheck should check class files for configurations added after license plugin is applied"() {
		given:
		File srcFile = emptyClassFile('src/mainTest/java/Class.java')
		buildFile << """
apply plugin: 'java'
apply plugin: 'org.dreamscale.license-ext'
apply plugin: 'org.dreamscale.test-ext'

license {
	ignoreFailures true
}
        """

		when:
		BuildResult result = run("licenseCheck", "--info")

		then:
		result.output =~ /Missing header in: .*${srcFile.name}/
	}

}
