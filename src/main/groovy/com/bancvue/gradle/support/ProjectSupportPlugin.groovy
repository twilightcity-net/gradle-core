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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class ProjectSupportPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'com.bancvue.project-support'

	private static final String TASK_GROUP_NAME = 'Utilities'

	private Project project

	@Override
	void apply(Project project) {
		this.project = project
		addPrintClasspathTask()
		addClearGroupCacheTasks()
	}

	private void addPrintClasspathTask() {
		Task printClasspath = project.tasks.create('printClasspath', PrintClasspath)
		printClasspath.group = TASK_GROUP_NAME
	}

	private void addClearGroupCacheTasks() {
		ClearArtifactCache clearCacheTask = project.task("clearGroupCache", type: ClearArtifactCache)
		clearCacheTask.group = TASK_GROUP_NAME
		clearCacheTask.groupName = project.group

		ClearArtifactCache clearCacheForProjectTask = project.task("clearGroupCacheForProject", type: ClearArtifactCache)
		clearCacheForProjectTask.group = TASK_GROUP_NAME
		clearCacheForProjectTask.groupName = project.group
		clearCacheForProjectTask.restrictToProjectDependencies = true
	}

}
