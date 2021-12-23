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
package net.twilightcity.gradle.support


import org.gradle.testkit.runner.BuildResult

class PrintClasspathIntegrationSpecification extends net.twilightcity.gradle.test.AbstractPluginIntegrationSpecification {

	def "should print compile and runtime classpaths to console for all source sets"() {
		given:
		buildFile << """
apply plugin: 'java'
dependencies {
    compile localGroovy()
}
task printClasspath(type: net.twilightcity.gradle.support.PrintClasspath)
        """

		when:
		BuildResult result = run("printClasspath")

		then:
		String output = result.output
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
task printClasspath(type: net.twilightcity.gradle.support.PrintClasspath)
        """

		when:
		BuildResult result = run("printClasspath", "-PsourceSetName=main")

		then:
		String output = result.output
		output =~ /main.compileClasspath/
		output =~ /main.runtimeClasspath/
		!(output =~ /test.compileClasspath/)
		!(output =~ /test.runtimeClasspath/)
	}

}
