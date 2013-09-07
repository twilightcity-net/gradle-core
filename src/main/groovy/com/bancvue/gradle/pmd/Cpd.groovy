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
package com.bancvue.gradle.pmd

import groovy.util.slurpersupport.GPathResult
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask

import javax.inject.Inject

class Cpd extends SourceTask implements VerificationTask {

	/**
	 * The class path containing the PMD library to be used.
	 */
	@InputFiles
	FileCollection cpdClasspath
	@Input
	int minimumTokenCount
	@Input
	boolean ignoreLiterals
	@Input
	boolean ignoreIdentifiers
	@Input
	File reportDir
	/**
	 * Whether or not to allow the build to continue if there are warnings.
	 *
	 * Example: ignoreFailures = true
	 */
	boolean ignoreFailures
	private final IsolatedAntBuilder antBuilder

	@Inject
	Cpd(IsolatedAntBuilder antBuilder) {
		this.antBuilder = antBuilder
	}

	@TaskAction
	void run() {
		File cpdOutputFile = new File(getReportDir(), 'cpd.xml')
		cpdOutputFile.parentFile.mkdirs()

		executeCpdAndWriteResultsToXmlFile(cpdOutputFile)
		failIfCpdViolationsExceedThresholdElseLog(cpdOutputFile)
	}

	private void executeCpdAndWriteResultsToXmlFile(File cpdOutputFile) {
		Map antCpdArgs = [
				ignoreLiterals: getIgnoreLiterals(),
				ignoreIdentifiers: getIgnoreIdentifiers(),
				minimumtokencount: getMinimumTokenCount(),
				format: 'xml',
				outputfile: cpdOutputFile.absolutePath
		]

		antBuilder.withClasspath(getCpdClasspath()).execute {
			ant.taskdef(name: 'cpd', classname: 'net.sourceforge.pmd.cpd.CPDTask')
			ant.cpd(antCpdArgs) {
				getSource().addToAntBuilder(ant, 'fileset', FileCollection.AntType.FileSet)
			}
		}
	}

	private void failIfCpdViolationsExceedThresholdElseLog(File cpdOutputFile) {
		if (isCpdViolationDetected(cpdOutputFile)) {
			String message = "CPD violations found.  See the report at: ${cpdOutputFile}"

			if (getIgnoreFailures()) {
				logger.warn(message)
			} else {
				throw new GradleException(message)
			}
		}
	}

	private boolean isCpdViolationDetected(File cpdOutputFile) {
		GPathResult pmdCpd = new XmlSlurper().parseText(cpdOutputFile.text)
		!pmdCpd.children().isEmpty()
	}

}
