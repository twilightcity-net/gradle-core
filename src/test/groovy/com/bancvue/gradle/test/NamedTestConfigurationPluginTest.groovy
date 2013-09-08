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
package com.bancvue.gradle.test

import org.gradle.api.Task
import org.junit.Before
import org.junit.Test

class NamedTestConfigurationPluginTest extends AbstractPluginTest {

	static class FunctionalTestPlugin extends NamedTestConfigurationPlugin {

		FunctionalTestPlugin() {
			super('functionalTest')
		}
	}

	NamedTestConfigurationPluginTest() {
		super("functional-test")
	}

	@Before
	void setUp() {
		project.file('src/functionalTest').mkdirs()
	}

	@Override
	protected void applyPlugin() {
		new FunctionalTestPlugin().apply(project)
	}

	@Test
	void apply_ShouldAddFunctionalTestSourceSet() {
		applyPlugin()

		assert project.sourceSets.functionalTest
	}

	@Test
	void apply_ShouldAddFunctionalTestConfigurations() {
		applyPlugin()

		assert project.configurations.functionalTest
		assert project.configurations.functionalTestCompile
		assert project.configurations.functionalTestCompile.extendsFrom.contains(project.configurations.testCompile)
		assert project.configurations.functionalTestRuntime
		assert project.configurations.functionalTestRuntime.extendsFrom.contains(project.configurations.testRuntime)
	}

	@Test
	void apply_ShouldNotAddFunctionalTestConfiguration_IfSrcDirDoesNotExist() {
		project.file('src/functionalTest').deleteDir()

		applyPlugin()

		assert !project.configurations.findByName('functionalTest')
		assert !project.tasks.findByName('functionalTest')
	}

	@Test
	void apply_ShouldCreateFunctionalTestTask() {
		applyPlugin()

		Task functionalTestTask = project.tasks.getByName('functionalTest')
		assert functionalTestTask
		assert functionalTestTask.group == 'Verification'
		assert functionalTestTask.testClassesDir == project.sourceSets.functionalTest.output.classesDir
		assert functionalTestTask.classpath == project.sourceSets.functionalTest.runtimeClasspath
		assert functionalTestTask.reports.html.destination == new File(project.buildDir, 'reports/functionalTests')
		assert functionalTestTask.reports.junitXml.destination == new File(project.buildDir, 'functionalTest-results')
	}

	@Test
	void apply_ShouldConfigureFunctionalTestToRunAfterUnitTest() {
		applyPlugin()

		Task functionalTestTask = project.tasks.getByName('functionalTest')
		use(TaskExtensions) {
			functionalTestTask.assertMustRunAfter('test')
		}
	}

}
