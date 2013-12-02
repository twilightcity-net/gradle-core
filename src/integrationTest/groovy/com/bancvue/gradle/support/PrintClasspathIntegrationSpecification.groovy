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
package com.bancvue.gradle.support

import com.bancvue.gradle.test.AbstractPluginIntegrationSpecification
import org.gradle.testkit.functional.ExecutionResult

class PrintClasspathIntegrationSpecification extends AbstractPluginIntegrationSpecification {

	def "should print compile and runtime classpaths to console for all source sets"() {
		given:
		buildFile << """
apply plugin: 'java'
dependencies {
    compile localGroovy()
}
task printClasspath(type: com.bancvue.gradle.support.PrintClasspath)
        """

		when:
		ExecutionResult result = run("printClasspath")

		then:
		String output = result.standardOutput
		output =~ /main.compileClasspath/
		output =~ /main.runtimeClasspath/
		output =~ /test.compileClasspath/
		output =~ /test.runtimeClasspath/
		output =~ /groovy-all.*jar/
	}

	def "should filter source set by name"() {
		given:
		buildFile << """
apply plugin: 'java'
task printClasspath(type: com.bancvue.gradle.support.PrintClasspath)
        """

		when:
		ExecutionResult result = run("printClasspath", "-PsourceSetName=main")

		then:
		String output = result.standardOutput
		output =~ /main.compileClasspath/
		output =~ /main.runtimeClasspath/
		!(output =~ /test.compileClasspath/)
		!(output =~ /test.runtimeClasspath/)
	}

}
