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

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.reporting.Report
import org.gradle.api.tasks.SourceSet
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

@Slf4j
class JacocoReportExt extends JacocoReport {

	private static final class ReportDirectoryCollector {

		private JacocoReportExt report

		ReportDirectoryCollector(JacocoReportExt report) {
			this.report = report
		}

		private Set<SourceSet> getAllMainSourceSets() {
			projects.collect { Project project ->
				project.sourceSets.findAll { SourceSet sourceSet ->
					sourceSet.name.startsWith("main")
				}
			}.flatten() as Set<SourceSet>
		}

		private Set<Project> getProjects() {
			report.includeSubProjects ? report.project.allprojects : [report.project]
		}

		private Set<SourceSet> getSourceSetsOrDefaults() {
			report.internalSourceSets ? report.internalSourceSets : getAllMainSourceSets()
		}

		FileCollection collectSourceDirs() {
			FileCollection sourceDirs = report.sourceDirectories
			if (sourceDirs == null) {
				sourceDirs = report.project.files()
			}

			getSourceSetsOrDefaults().each { SourceSet sourceSet ->
				sourceDirs += report.project.files(sourceSet.allJava.srcDirs)
			}
			sourceDirs
		}

		FileCollection collectClassDirs() {
			FileCollection classDirs = report.classDirectories
			if (classDirs == null) {
				classDirs = report.project.files()
			}

			getSourceSetsOrDefaults().each { SourceSet sourceSet ->
				classDirs += sourceSet.output
			}
			classDirs
		}
	}

	// NOTE: the more appropriate name for this would be 'sourceSets'; however, in the context of a gradle build script,
	// this ends up intercepting calls which are meant for the project.  for example, if we declared a sourceSets
	// variable here, the following would not work as intended when declared in build.gradle...
	// jacocoTestReport {
	//   reports.additionalSourceDirs files(sourceSets.main.java.srcDirs)
	// }
	//
	Set<SourceSet> internalSourceSets = []
	boolean includeSubProjects = false

	JacocoReportExt() {
		group = TestExtPlugin.VERIFICATION_GROUP_NAME
		getProject().afterEvaluate {
			ReportDirectoryCollector collector = new ReportDirectoryCollector(this)
			sourceDirectories = collector.collectSourceDirs()
			classDirectories = collector.collectClassDirs()
		}
	}

	@Override
	void sourceSets(SourceSet... sourceSets) {
		sourceSets.each {
			this.internalSourceSets << it
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
			report.enabled = true
			report.conventionMapping.with {
				destination = {
					JacocoReportExt.getReportDestinationFile(jacocoReportsDir, report, reportCategory)
				}
			}
		}
	}

	private static File getReportDestinationFile(File jacocoReportsDir, Report report, String reportDirName) {
		if (report.outputType == Report.OutputType.DIRECTORY) {
			new File(jacocoReportsDir, "${reportDirName}/${report.name}")
		} else {
			new File(jacocoReportsDir, "${reportDirName}/${reportDirName}.${report.name}")
		}
	}

}
