package com.bancvue.gradle.pmd

import com.bancvue.gradle.test.AbstractPluginIntegrationSpecification

class AbstractCpdPluginIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	protected void classFileWithDuplicateTokens(String fileName, int dupTokenCount) {
		File file = file(fileName)
		String className = file.name.replaceFirst(~/\.[^\.]+$/, '')
		file << """
public class ${className} {
	void violate() {
		int x = 0;
		${"x++; " * dupTokenCount}
	}
}
"""
	}

	protected void assertDuplicationDetected() {
		assert file("build/reports/cpd/all.xml").text =~ /duplication/
		assert file("build/reports/cpd/all.html").exists()
	}

}
