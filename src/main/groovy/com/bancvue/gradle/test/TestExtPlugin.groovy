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

import com.bancvue.gradle.GradlePluginMixin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test

@Mixin(GradlePluginMixin)
class TestExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = "test-ext"
	static final String VERIFICATION_GROUP_NAME = "Verification"

	static Jar getMainTestJarTaskOrNullIfMainTestConfigurationNotDefined(Project project) {
		project.tasks.findByName("mainTestJar")
	}


	private Project project

	@Override
	void apply(Project project) {
		this.project = project
		project.apply(plugin: "java")
		addMainTestConfigurationIfMainTestDirDefined()
		updateTestLoggersToWriteStackTracesOnTestFailure()
		udpateTestLoggersToWriteSkippedTestEvents()
		addStyledTestOutputTask()
	}

	private void addMainTestConfigurationIfMainTestDirDefined() {
		if (project.file("src/mainTest").exists()) {
			addMainTestConfiguration()
			addMainTestSourceSet()
			addMainTestJarTask()
			updateTestSourceSetToIncludeMainTestConfiguration()
		}
	}

	private void addMainTestConfiguration() {
		createNamedConfigurationExtendingFrom("mainTest", "compile", "runtime")
	}

	private void addMainTestSourceSet() {
		project.sourceSets {
			mainTest {
				compileClasspath = main.output + project.configurations.mainTestCompile
				runtimeClasspath = mainTest.output + main.output + project.configurations.mainTestRuntime
			}
		}
	}

	private void addMainTestJarTask() {
		Task mainTestJarTask = project.tasks.create("mainTestJar", Jar)
		mainTestJarTask.configure {
			group = "Build"
			classifier = "test"
			description = "Assembles a jar archive containing the test sources."
			from project.sourceSets.mainTest.output
		}
	}

	private void updateTestSourceSetToIncludeMainTestConfiguration() {
		project.sourceSets {
			test {
				compileClasspath = mainTest.output + main.output +
						project.configurations.mainTestCompile + compileClasspath
				runtimeClasspath = test.output + mainTest.output + main.output +
						project.configurations.mainTestRuntime + runtimeClasspath
			}
		}
	}

	private void updateTestLoggersToWriteStackTracesOnTestFailure() {
		project.tasks.withType(Test) { Test test ->
			test.testLogging.exceptionFormat = "full"
			test.testLogging.stackTraceFilters("groovy")
		}
	}

	private void udpateTestLoggersToWriteSkippedTestEvents() {
		project.tasks.withType(Test) { Test test ->
			test.testLogging.events("skipped")
		}
	}

	private void addStyledTestOutputTask() {
		StyledTestOutput stoTask = project.tasks.create("styledTestOutput", StyledTestOutput)
		stoTask.configure {
			group = VERIFICATION_GROUP_NAME
			description = "Modifies build to output test results incrementally"
		}

		project.tasks.withType(Test) { Test test ->
			test.mustRunAfter stoTask
		}
	}

}
