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
package org.dreamscale.gradle.pmd

import org.dreamscale.gradle.ResourceResolver
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.IsolatedAntBuilder
import org.gradle.api.reporting.Reporting
import org.gradle.api.reporting.SingleFileReport
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class Cpd extends SourceTask implements VerificationTask, Reporting<CpdReports> {

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
	String cpdXsltPath
	@Nested
	private final CpdReportsImpl reports
	/**
	 * Whether or not to allow the build to continue if there are warnings.
	 *
	 * Example: ignoreFailures = true
	 */
	boolean ignoreFailures
	private IsolatedAntBuilder antBuilder
	private ResourceResolver resourceResolver

	@Inject
	Cpd(Instantiator instantiator, IsolatedAntBuilder antBuilder) {
		reports = instantiator.newInstance(CpdReportsImpl, this)
		this.antBuilder = antBuilder
		this.resourceResolver = ResourceResolver.create(project)
	}

	@Override
	CpdReports getReports() {
		reports
	}

	@Override
	CpdReports reports(Closure closure) {
		reports.configure(closure)
	}

	@Override
	CpdReports reports(Action a) {
		reports.configure {
			a.execute(it)
		}
	}

	@TaskAction
	void run() {
		assertHtmlReportDisabledIfXmlReportDisabled()
		if (reports.xml.enabled) {
			executeCpdReport()
		}
	}

	private void assertHtmlReportDisabledIfXmlReportDisabled() {
		if (!reports.xml.enabled && reports.html.enabled) {
			throw new GradleException("Invalid CPD configuration '${name}' - xml report must be enabled if html report enabled")
		}
	}

	private void executeCpdReport() {
		ensureParentDirectoryExists(reports.xml)
		executeCpdAndWriteResultsToXmlFile()
		failIfCpdViolationsExceedThresholdElseLog()
	}

	private void ensureParentDirectoryExists(SingleFileReport report) {
		if (!report.destination) {
			throw new GradleException("Invalid CPD report '${report.name}', no destination directory defined")
		}
		report.destination.parentFile.mkdirs()
	}

	private void executeCpdAndWriteResultsToXmlFile() {
		ensureParentDirectoryExists(reports.xml)

		Map antCpdArgs = [
				ignoreLiterals: getIgnoreLiterals(),
				ignoreIdentifiers: getIgnoreIdentifiers(),
				minimumtokencount: getMinimumTokenCount(),
				format: 'xml',
				outputfile: reports.xml.destination
		]

		antBuilder.withClasspath(getCpdClasspath()).execute {
			ant.taskdef(name: 'cpd', classname: 'net.sourceforge.pmd.cpd.CPDTask')
			ant.cpd(antCpdArgs) {
				getSource().addToAntBuilder(ant, 'fileset', FileCollection.AntType.FileSet)
			}
		}
		createHtmlReportIfEnabled()
	}

	private void createHtmlReportIfEnabled() {
		if (reports.html.enabled) {
			ensureParentDirectoryExists(reports.html)
			File cpdXsltFile = acquireCpdXsltFile()

			ant.xslt(in: reports.xml.destination,
					style: cpdXsltFile,
					out: reports.html.destination)
		}
	}

	private File acquireCpdXsltFile() {
		URL cpdXsltURL = resourceResolver.getResourceURL(getCpdXsltPath())
		File cpdXsltFile = new File(project.buildDir, "pmd/cpdhtml.xslt")

		cpdXsltFile.parentFile.mkdirs()
		cpdXsltFile.write(cpdXsltURL.text)
		cpdXsltFile
	}

	private void failIfCpdViolationsExceedThresholdElseLog() {
		if (isCpdViolationDetected()) {
			File cpdReportFile = reports.html.enabled ? reports.html.destination : reports.xml.destination
			String message = "CPD violations found.  See the report at: ${cpdReportFile}"

			if (getIgnoreFailures()) {
				logger.warn(message)
			} else {
				throw new GradleException(message)
			}
		}
	}

	private boolean isCpdViolationDetected() {
		GPathResult pmdCpd = new XmlSlurper().parseText(reports.xml.destination.text)
		!pmdCpd.children().isEmpty()
	}

}
