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

import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet

class CpdPlugin extends AbstractCodeQualityPlugin<Cpd> {

	static final String PLUGIN_NAME = 'pmdcpd'

	@Override
	protected void beforeApply() {
		project.apply(plugin: 'pmd')
	}

	@Override
	protected String getToolName() {
		return "CPD"
	}

	@Override
	protected Class<Cpd> getTaskType() {
		return Cpd
	}

	@Override
	protected CodeQualityExtension createExtension() {
		PmdExtension pmdExtension = project.extensions.getByName('pmd')
		CodeQualityExtension cpdExtension = project.extensions.create("cpd", CpdExtension)
		cpdExtension.with {
			toolVersion = pmdExtension.toolVersion
		}
		cpdExtension
	}

	@Override
	protected void configureTaskDefaults(Cpd task, String baseName) {
		task.conventionMapping.with {
			cpdClasspath = {
				project.configurations['pmd']
			}
			cpdXsltPath = { extension.cpdXsltPath }
			minimumTokenCount = { extension.minimumTokenCount }
			ignoreLiterals = { extension.ignoreLiterals }
			ignoreIdentifiers = { extension.ignoreIdentifiers }
			ignoreFailures = { extension.ignoreFailures }
		}

		task.reports.all { report ->
			report.conventionMapping.with {
				enabled = { true }
				destination = { new File(extension.reportsDir, "${baseName}.${report.name}") }
			}
		}
	}

	@Override
	protected void configureForSourceSet(SourceSet sourceSet, Cpd task) {
		task.with {
			description = "Run CPD analysis for ${sourceSet.name} classes"
		}
		task.setSource(sourceSet.allJava)
	}

}
