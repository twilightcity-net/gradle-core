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

import org.dreamscale.gradle.multiproject.PostEvaluationNotifier
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.internal.file.UnionFileTree
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin
import org.gradle.api.tasks.SourceSet

class CpdPlugin extends AbstractCodeQualityPlugin<Cpd> {

	static final String PLUGIN_NAME = 'org.dreamscale.cpd'

	static final String UNIFIED_REPORT_TASK_NAME = "cpdAll"

	private static final PostEvaluationNotifier POST_EVAL_NOTIFIER = new PostEvaluationNotifier(
			{ List<Project> projects ->
				Project rootProject = projects[0].rootProject
				CpdPlugin cpdPlugin = rootProject.plugins.findPlugin(PLUGIN_NAME)
				if (cpdPlugin) {
					cpdPlugin.conditionallyCreateUnifiedReport(projects)
				}
			})



	private void conditionallyCreateUnifiedReport(List<Project> projects) {
		if (shouldCreateUnifiedReport()) {
			Set<File> cpdSourceFiles = getAllCpdSourceFiles(projects)
			Cpd task = project.tasks.create(name: UNIFIED_REPORT_TASK_NAME, type: Cpd,
					description: 'Run CPD analysis for all sources', overwrite: true)
			task.source(cpdSourceFiles)
			project.plugins.withType(JavaBasePlugin) {
				project.tasks.findByName('check').dependsOn(task)
			}
		}
	}

	private Set<File> getAllCpdSourceFiles(List<Project> projects) {
		UnionFileTree allCpdSource = new UnionFileTree()
		projects.each { Project project ->
			project.tasks.withType(Cpd).each { Cpd cpd ->
				allCpdSource.add(cpd.source)
			}
		}
		allCpdSource.files
	}


	@Override
	protected void beforeApply() {
		POST_EVAL_NOTIFIER.addProject(project)
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
		CodeQualityExtension cpdExtension = project.extensions.create("cpd", CpdExtension)
		cpdExtension.toolVersion = "5.1.1"
		cpdExtension
	}

	private CpdExtension getCpdExtension() {
		extension as CpdExtension
	}

	protected void configureConfiguration(Configuration config) {
		config.incoming.beforeResolve {
			DependencySet dependencies = config.dependencies
			if (dependencies.isEmpty()) {
				dependencies.add(project.dependencies.create(getPmdDependencyGav()))
			}
		}
	}

	private String getPmdDependencyGav() {
		int toolMajorVersion = extension.toolVersion.replaceFirst(/\..*/, "") as int
		String pmdGroupId = (toolMajorVersion < 5) ? "pmd" : "net.sourceforge.pmd"
		"${pmdGroupId}:pmd:${extension.toolVersion}"
	}

	@Override
	protected void configureTaskDefaults(Cpd task, String baseName) {
		task.conventionMapping.with {
			cpdClasspath = { project.configurations['cpd'] }
			cpdXsltPath = { cpdExtension.cpdXsltPath }
			minimumTokenCount = { cpdExtension.minimumTokenCount }
			ignoreLiterals = { cpdExtension.ignoreLiterals }
			ignoreIdentifiers = { cpdExtension.ignoreIdentifiers }
			ignoreFailures = { cpdExtension.ignoreFailures }
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
			setSource(sourceSet.allJava)
			onlyIf {
				shouldCreateUnifiedReport() == false
			}
		}
	}

	private boolean shouldCreateUnifiedReport() {
		CpdExtension extension = project.rootProject.extensions.getByName(CpdExtension.NAME)
		extension.createAllReport
	}

}
