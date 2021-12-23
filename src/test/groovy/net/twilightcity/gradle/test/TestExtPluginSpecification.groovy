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

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestStackTraceFilter

class TestExtPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		TestExtPlugin.PLUGIN_NAME
	}

	def "apply should write stack trace on test failure"() {
		when:
		applyPlugin()

		then:
		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')
		test.testLogging.exceptionFormat == TestExceptionFormat.FULL
		test.testLogging.stackTraceFilters.contains(TestStackTraceFilter.GROOVY)
	}

	def "apply should write skipped events"() {
		when:
		applyPlugin()

		then:
		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')
		test.testLogging.events.contains(TestLogEvent.SKIPPED)
	}

	def "apply should add styledTestOutput task and configure to execute before test tasks"() {
		when:
		applyPlugin()

		then:
		StyledTestOutput styledOutputTask = project.tasks.getByName('styledTestOutput')
		TaskCategory.assertMustRunBefore(styledOutputTask, 'test')
	}

}
