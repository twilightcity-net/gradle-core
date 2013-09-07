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


class TaskExtensions {

	static void assertMustRunAfter(Task self, String taskName) {
		Task task = self.getProject().getTasks().getByName(taskName)
		assertMustRunAfter(self, task)
	}

	static void assertMustRunAfter(Task self, Task task) {
		Set<? extends Task> dependencies = self.getMustRunAfter().getDependencies(self)
		assert dependencies.contains(task)
	}

	static void assertMustRunBefore(Task self, String taskName) {
		Task task = self.getProject().getTasks().getByName(taskName)
		assertMustRunBefore(self, task)
	}

	static void assertMustRunBefore(Task self, Task task) {
		Set<? extends Task> dependencies = task.getMustRunAfter().getDependencies(task)
		assert dependencies.contains(self)
	}

}
