package com.bancvue.gradle.license

import com.bancvue.gradle.test.AbstractPluginIntegrationTest
import org.junit.Test

class LicenseExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	/**************************************************************************************************************
	 * NOTE: if these test fail in an IDE, it may need to add 'headers/*' to the compiler settings so resources
	 * are copied appropriately
	 **************************************************************************************************************/

	@Test
	void shouldWriteLicenseHeaderToSourceFiles() {
		List<File> srcFiles = ["src/main/java", "src/mainTest/java", "src/test/java"].collect{ String path ->
			projectFS.file(path, "Class.java") << "class Class {}"
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
	void shouldNotFailIfBuildIsFirstCleaned() {
		File srcFile = projectFS.file('src/main/java/Class.java') << "class Class {}"

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
	void shouldUseAlternativeHeaderIfProvided() {
		File srcFile = projectFS.file('src/main/java/Class.java') << "class Class {}"
		projectFS.file('src/main/resources/ALT_HEADER') << "ALTERNATIVE HEADER"

		projectFS.buildFile() << """
ext {
	licenseHeaderResourcePath='/ALT_HEADER'
}

apply plugin: 'java'
apply plugin: 'license-ext'
        """

		run("assemble", "licenseFormat")

		String text = srcFile.text
		println text
		assert text =~ /ALTERNATIVE HEADER/
	}

}
