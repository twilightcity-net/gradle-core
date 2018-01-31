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
package org.dreamscale.gradle.test

import org.gradle.api.Task

class NamedTestConfigurationPluginSpecification extends AbstractPluginSpecification {

	static class FunctionalTestPlugin extends NamedTestConfigurationPlugin {

		FunctionalTestPlugin() {
			super('functionalTest')
		}
	}

	String getPluginName() {
		"functional-test"
	}

	void setup() {
		project.file('src/functionalTest').mkdirs()
	}

	@Override
	protected void applyPlugin() {
		new FunctionalTestPlugin().apply(project)
	}

	def "apply should add functionalTest source set"() {
		when:
		applyPlugin()

		then:
		project.sourceSets.functionalTest
	}

	def "apply should add functionalTest onfigurations"() {
		when:
		applyPlugin()

		then:
		project.configurations.functionalTest
		project.configurations.functionalTestCompile
		project.configurations.functionalTestCompile.extendsFrom.contains(project.configurations.sharedTestCompile)
		project.configurations.functionalTestRuntime
		project.configurations.functionalTestRuntime.extendsFrom.contains(project.configurations.sharedTestRuntime)
	}

	def "apply should not add functionalTest configuration if src dir does not exist"() {
		given:
		project.file('src/functionalTest').deleteDir()

		when:
		applyPlugin()

		then:
		!project.configurations.findByName('functionalTest')
		!project.tasks.findByName('functionalTest')
	}

	def "apply should create functionalTest task"() {
		when:
		applyPlugin()

		then:
		Task functionalTestTask = project.tasks.getByName('functionalTest')
		functionalTestTask
		functionalTestTask.group == 'Verification'
		functionalTestTask.testClassesDir == project.sourceSets.functionalTest.output.classesDir
		functionalTestTask.classpath == project.sourceSets.functionalTest.runtimeClasspath
		functionalTestTask.reports.html.destination == new File(project.buildDir, 'reports/functionalTests')
		functionalTestTask.reports.junitXml.destination == new File(project.buildDir, 'functionalTest-results')
	}

	def "apply should configure functionalTest to run after unit test"() {
		when:
		applyPlugin()

		then:
		Task functionalTestTask = project.tasks.getByName('functionalTest')
		TaskCategory.assertMustRunAfter(functionalTestTask, 'test')
	}

}
