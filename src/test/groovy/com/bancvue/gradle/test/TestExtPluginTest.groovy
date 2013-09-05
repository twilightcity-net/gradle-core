package com.bancvue.gradle.test

import com.bancvue.gradle.AbstractPluginTest
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestStackTraceFilter
import org.junit.Test

class TestExtPluginTest extends AbstractPluginTest {

	TestExtPluginTest() {
		super(TestExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldWriteStackTraceOnTestFailure() {
		applyPlugin()

		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')

		assert test.testLogging.exceptionFormat == TestExceptionFormat.FULL
		assert test.testLogging.stackTraceFilters.contains(TestStackTraceFilter.GROOVY)
	}

	@Test
	void apply_ShouldWriteSkippedEvents() {
		applyPlugin()

		org.gradle.api.tasks.testing.Test test = project.tasks.getByName('test')

		assert test.testLogging.events.contains(TestLogEvent.SKIPPED)
	}

	@Test
	void apply_ShouldAddStyledTestOutputTaskAndConfigureToExecuteBeforeTestTasks() {
		applyPlugin()

		StyledTestOutput styledOutputTask = project.tasks.getByName('styledTestOutput')
		use(TaskExtensions) {
			styledOutputTask.assertMustRunBefore('test')
		}
	}

}
