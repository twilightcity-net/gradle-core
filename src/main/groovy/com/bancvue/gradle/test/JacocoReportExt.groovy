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

import org.gradle.api.reporting.Report
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

import javax.inject.Inject

class JacocoReportExt extends JacocoReport {

	Set<SourceSet> sourceSets = []

	@Inject
	JacocoReportExt(Instantiator instantiator) {
		super(instantiator)

		group = TestExtPlugin.VERIFICATION_GROUP_NAME
		getProject().afterEvaluate {
			configureSourceAndClassDirectoriesFromSourceSets()
		}
	}

	private Set<SourceSet> getAllMainSourceSets() {
		project.sourceSets.findAll { SourceSet sourceSet ->
			sourceSet.name.startsWith("main")
		}
	}

	private Set<SourceSet> getSourceSetsOrDefaults() {
		sourceSets ? sourceSets : getAllMainSourceSets()
	}

	private void configureSourceAndClassDirectoriesFromSourceSets() {
		getSourceSetsOrDefaults().each { SourceSet sourceSet ->
			if (this.sourceDirectories == null) {
				this.sourceDirectories = sourceSet.allJava
			} else {
				this.sourceDirectories = this.sourceDirectories + sourceSet.allJava
			}
			if (this.classDirectories == null) {
				this.classDirectories = sourceSet.output
			} else {
				this.classDirectories = this.classDirectories + sourceSet.output
			}
		}
	}

	@Override
	void sourceSets(SourceSet... sourceSets) {
		sourceSets.each {
			this.sourceSets << it
		}
	}

	File getJacocoReportsDir() {
		JacocoPluginExtension extension = project.extensions.getByName(JacocoPluginExtension.TASK_EXTENSION_NAME)
		extension.reportsDir
	}

	void setReportCategory(String reportCategory) {
		if (!description) {
			description = "Generates the ${reportCategory} coverage report."
		}

		reports.all { Report report ->
			report.conventionMapping.with {
				enabled = { true }
				destination = {
					getReportDestinationClosure(report, reportCategory)
				}
			}
		}
	}

	private File getReportDestinationClosure(Report report, String reportDirName) {
		if (report.outputType == Report.OutputType.DIRECTORY) {
			new File(jacocoReportsDir, "${reportDirName}/${report.name}")
		} else {
			new File(jacocoReportsDir, "${reportDirName}/${reportDirName}.${report.name}")
		}
	}

}
