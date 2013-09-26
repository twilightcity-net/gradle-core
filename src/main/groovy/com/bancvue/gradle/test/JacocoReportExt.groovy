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
import org.gradle.api.file.FileCollection
import org.gradle.api.reporting.Report
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

import javax.inject.Inject

@Slf4j
class JacocoReportExt extends JacocoReport {

	// NOTE: the more appropriate name for this would be 'sourceSets'; however, in the context of a gradle build script,
	// this ends up intercepting calls which are meant for the project.  for example, if we declared a sourceSets
	// variable here, the following would not work as intended when declared in build.gradle...
	// jacocoTestReport {
	//   reports.additionalSourceDirs files(sourceSets.main.java.srcDirs)
	// }
	//
	Set<SourceSet> internalSourceSets = []

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
		internalSourceSets ? internalSourceSets : getAllMainSourceSets()
	}

	private void configureSourceAndClassDirectoriesFromSourceSets() {
		getSourceSetsOrDefaults().each { SourceSet sourceSet ->
			log.info("Adding source and output directories from sourceSet ${sourceSet.name}")
			FileCollection srcDirs = project.files(sourceSet.allJava.srcDirs)
			if (this.sourceDirectories == null) {
				this.sourceDirectories = srcDirs
			} else {
				this.sourceDirectories = this.sourceDirectories + srcDirs
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
