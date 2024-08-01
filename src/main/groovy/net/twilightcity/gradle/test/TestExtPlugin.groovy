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
package net.twilightcity.gradle.test

import org.betterdevxp.gradle.test.DynamicTestSetsPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.impldep.org.testng.internal.thread.IThreadFactory

class TestExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = "net.twilightcity.test-ext"
	static final String VERIFICATION_GROUP_NAME = "Verification"


	private Project project

	@Override
	void apply(Project project) {
		this.project = project
		project.pluginManager.apply(JavaPlugin.class)
		project.pluginManager.apply(DynamicTestSetsPlugin.class)
		updateTestLoggersToWriteStackTracesOnTestFailure()
		udpateTestLoggersToWriteSkippedTestEvents()
		addStyledTestOutputTask()
		configureTestTimerReporter()
	}

	private void updateTestLoggersToWriteStackTracesOnTestFailure() {
		project.tasks.withType(Test).configureEach { Test test ->
			test.testLogging.exceptionFormat = "full"
			test.testLogging.stackTraceFilters("groovy")
		}
	}

	private void udpateTestLoggersToWriteSkippedTestEvents() {
		project.tasks.withType(Test).configureEach { Test test ->
			test.testLogging.events("skipped")
		}
	}

	private void addStyledTestOutputTask() {
		TaskProvider<StyledTestOutput> stoTask = project.tasks.register("styledTestOutput", StyledTestOutput)
		stoTask.configure {
			group = VERIFICATION_GROUP_NAME
			description = "Modifies build to output test results incrementally"
		}

		project.tasks.withType(Test).configureEach { Test test ->
			test.mustRunAfter stoTask
		}
	}

	private void configureTestTimerReporter() {
		def testTimerReportThreshold = project.findProperty("testTimerReportThreshold")
		if (testTimerReportThreshold) {
			def threshold = Integer.parseInt(testTimerReportThreshold.toString())
			project.tasks.withType(Test).configureEach {
				afterTest { descriptor, result ->
					def totalTime = result.endTime - result.startTime
					if (totalTime > threshold) {
						println "$descriptor.className.$descriptor.name took $totalTime ms"
					}
				}
			}
		}
	}

}
