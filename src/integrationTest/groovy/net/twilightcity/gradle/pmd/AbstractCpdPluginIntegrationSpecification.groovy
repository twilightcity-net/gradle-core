/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle.pmd

import net.twilightcity.gradle.test.AbstractPluginIntegrationSpecification

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
