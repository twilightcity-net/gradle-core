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
package com.bancvue.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet


class IdeExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'ide-ext'

	private static final String IDE_GROUP_NAME = 'IDE'

	private Project project

	public void apply(Project project) {
		this.project = project
		project.apply(plugin: 'java')
		applyIdeaPlugin()
		applyEclipsePlugin()
	}

	private void applyIdeaPlugin() {
		project.apply(plugin: 'idea')
		addRefreshIdeaTask()
		addRefreshIdeaModuleTask()
		updateIdeaSourcePathAndTestScopePriorToModuleTaskExecution()
	}

	private void addRefreshIdeaTask() {
		Task refreshIdea = project.tasks.create('refreshIdea')
		Task cleanIdea = project.tasks.getByName('cleanIdea')
		Task idea = project.tasks.getByName('idea')

		refreshIdea.group = IDE_GROUP_NAME
		refreshIdea.description = 'Clean and generate IDEA project, workspace and module files'
		refreshIdea.dependsOn(cleanIdea, idea)
	}

	private void addRefreshIdeaModuleTask() {
		Task refreshIdeaModule = project.tasks.create('refreshIdeaModule')
		Task cleanIdeaModule = project.tasks.getByName('cleanIdeaModule')
		Task ideaModule = project.tasks.getByName('ideaModule')

		refreshIdeaModule.group = IDE_GROUP_NAME
		refreshIdeaModule.description = 'Clean and generate IDEA module file'
		refreshIdeaModule.dependsOn(cleanIdeaModule, ideaModule)
	}

	private void updateIdeaSourcePathAndTestScopePriorToModuleTaskExecution() {
		Task ideaModule = project.tasks.getByName('ideaModule')

		ideaModule.doFirst {
			updateIdeaSourcePathAndTestScope()
		}
	}

	private void updateIdeaSourcePathAndTestScope() {
		resetIdeaModuleTestPaths()
		addTestConfigurationsToIdeaTestScope()
	}

	private void resetIdeaModuleTestPaths() {
		project.idea {
			module.conventionMapping.testSourceDirs = { getTestSourceDirs() }
		}
	}

	private Set<File> getTestSourceDirs() {
		project.sourceSets.findAll { SourceSet sourceSet ->
			sourceSet.name =~ /(?i)test/
		}.collect { SourceSet sourceSet ->
			sourceSet.allSource.srcDirs
		}.flatten()
	}

	private void addTestConfigurationsToIdeaTestScope() {
		project.idea {
			module {
				scopes.TEST.plus = getTestRuntimeConfigurations()
			}
		}
	}

	private Set<Configuration> getTestRuntimeConfigurations() {
		Set runtimeConfigurations = project.configurations.findAll { Configuration config ->
			config.name =~ /(?i)testruntime$/
		} as Set
		runtimeConfigurations
	}

	private void applyEclipsePlugin() {
		project.apply(plugin: 'eclipse')
		addRefreshEclipseTask()
		updateEclipseSourcePathAndClassPathAfterProjectEvaluation()
	}

	private void addRefreshEclipseTask() {
		Task refreshEclipse = project.tasks.create('refreshEclipse')
		Task cleanEclipse = project.tasks.getByName('cleanEclipse')
		Task eclipse = project.tasks.getByName('eclipse')

		refreshEclipse.group = IDE_GROUP_NAME
		refreshEclipse.description = 'Clean and generate all Eclipse files'
		refreshEclipse.dependsOn(cleanEclipse, eclipse)
	}


	private void updateEclipseSourcePathAndClassPathAfterProjectEvaluation() {
		project.afterEvaluate {
			updateEclipseSourcePathAndClassPath()
		}
	}

	private void updateEclipseSourcePathAndClassPath() {
		project.eclipse {
			classpath {
				plusConfigurations += getTestRuntimeConfigurations()
			}
		}
	}

}
