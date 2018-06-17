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

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.testing.DecoratingTestDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory

import static org.gradle.internal.logging.text.StyledTextOutput.Style

/**
 * Adapted (copied) from https://github.com/brunodecarvalho/gradle-plugins/blob/master/colored-test-output.gradle
 */
class StyledTestOutput extends DefaultTask {

	@TaskAction
	void addStyledOutputToTestTasks() {
		StyledTextOutput out = project.services.get(StyledTextOutputFactory).create("styled-test-output")
		out.style(Style.Normal)

		project.tasks.withType(Test) { Test test ->
			test.beforeSuite { DecoratingTestDescriptor descriptor ->
				if (descriptor.className) {
					out.println()
					out.println(descriptor.className)
				}
			}

			test.afterTest { TestDescriptor descriptor, TestResult result ->
				Style style = StyledTestOutput.getStyleForResult(result)
				out.withStyle(style).println("  ${descriptor.name}")
			}
		}
	}

	private static Style getStyleForResult(TestResult result) {
		Style style = Style.Identifier

		if (result.failedTestCount > 0) {
			style = Style.Failure
		} else if (result.skippedTestCount > 0) {
			style = Style.ProgressStatus
		}
		style
	}

}
