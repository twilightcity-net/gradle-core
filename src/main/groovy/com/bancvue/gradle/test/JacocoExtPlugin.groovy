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

import com.bancvue.gradle.multiproject.PostEvaluationNotifier
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = "org.dreamscale.jacoco-ext"


	private Project project

	private static final PostEvaluationNotifier POST_EVAL_NOTIFIER = new PostEvaluationNotifier({ Project aProject ->
		Task mergeAll = aProject.tasks.findByName("jacocoAllMerge")
		if (mergeAll) {
			aProject.subprojects.each { Project subProject ->
				mergeAll.dependsOn(subProject.tasks.withType(JacocoMerge))
			}
		}
	})

	@Override
	void apply(Project project) {
		this.project = project
		project.apply(plugin: "jacoco")
		addPluginExtension()
		addJacocoTestReportsForAllTestTasks()
		addConsolidatedJacocoReportTask()
		addCoverageTask()

		POST_EVAL_NOTIFIER.addProject(project)
	}

	private void addPluginExtension() {
		project.extensions.create(JacocoExtExtension.NAME, JacocoExtExtension)
	}

	private JacocoExtExtension getPluginExtension() {
		project.extensions.getByName(JacocoExtExtension.NAME) as JacocoExtExtension
	}

	private void addJacocoTestReportsForAllTestTasks() {
		project.tasks.withType(Test) { Test task ->
			createJacocoReportForTestTask(task)
		}
	}

	private void createJacocoReportForTestTask(Test testTask) {
		String reportTaskName = "jacoco${testTask.name.capitalize()}Report"
		JacocoReportExt reportTask = project.tasks.create([name: reportTaskName, overwrite: true, type: JacocoReportExt])

		reportTask.reportCategory = testTask.name
		reportTask.executionData(testTask)
	}

	private FileCollection getExecutionDataForMergeAllTaskAsFileCollection(JacocoReportExt excludedReport) {
		Set files = []
		getProjectsToIncludeForMergeAllTask().each { Project aProject ->
			getAllReportsExcept(aProject, excludedReport).each { JacocoReport report ->
				files.addAll(report.executionData.findAll {
					it.exists()
				})
			}
		}
		project.files(files)
	}

	private Set<Project> getProjectsToIncludeForMergeAllTask() {
		shouldIncludeSubProjectsInAllReport() ? project.allprojects : [project]
	}

	private boolean shouldIncludeSubProjectsInAllReport() {
		getPluginExtension().includeSubProjectsInAllReport
	}

	private Set<JacocoReport> getAllReportsExcept(Project project, JacocoReport excludeReport) {
		project.tasks.withType(JacocoReport).findAll { JacocoReport report ->
			!report.is(excludeReport)
		}
	}

	private JacocoMerge createJacocoMergeAllTask(JacocoReportExt excludeReport) {
		JacocoMerge mergeAll = project.tasks.create("jacocoAllMerge", JacocoMerge)
		mergeAll.configure {
			ext.visible = false
			mustRunAfter(getAllReportsExcept(project, excludeReport))
		}
		mergeAll.conventionMapping.with {
			executionData = { getExecutionDataForMergeAllTaskAsFileCollection(excludeReport) }
		}
		mergeAll
	}

	private void addConsolidatedJacocoReportTask() {
		JacocoReportExt reportAll = project.tasks.create("jacocoAllReport", JacocoReportExt)
		JacocoMerge mergeAll = createJacocoMergeAllTask(reportAll)

		reportAll.configure {
			description = "Generates a consolidated coverage report for the entire project (including any sub-projects)"
			reportCategory = "all"
			dependsOn(mergeAll)
		}
		reportAll.conventionMapping.with {
			includeSubProjects = { shouldIncludeSubProjectsInAllReport() }
			executionData = { project.files(mergeAll.destinationFile) }
		}
	}

	private void addCoverageTask() {
		Task coverage = project.tasks.create("coverage")

		coverage.configure {
			group = TestExtPlugin.VERIFICATION_GROUP_NAME
			description = "Execute all tests and generate coverage reports"
			Task check = project.tasks.getByName("check")
			dependsOn(check)
			mustRunAfter(check)
		}
		project.tasks.withType(JacocoReport) { JacocoReport report ->
			coverage.dependsOn(report)
		}
	}

}
