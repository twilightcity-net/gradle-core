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

	static final String PLUGIN_NAME = 'org.dreamscale.ide-ext'

	private static final String IDE_GROUP_NAME = 'IDE'

	private Project project

	public void apply(Project project) {
		this.project = project
		project.apply(plugin: 'java')
		applyIdeaPlugin()
		applyEclipsePlugin()
		project.ext['updateIdePaths'] = true
	}

	private boolean shouldUpdateIdePaths() {
		project.ext['updateIdePaths'] == true
	}

	private void applyIdeaPlugin() {
		project.apply(plugin: 'idea')
		addRefreshIdeaTask()
		addRefreshIdeaModuleTask()
		moveCleanIdeaWorkspaceToIdeGroup()
		updateIdeaSourcePathAndTestScopePriorToModuleTaskExecution()
	}

	/**
	 * no task depends on cleanIdeaWorkspace and it's getting listed in group 'Other', so
	 * move it to the IDE group
	 */
	private void moveCleanIdeaWorkspaceToIdeGroup() {
		Task cleanIdeaWorkspace = project.tasks.findByName('cleanIdeaWorkspace')
		if (cleanIdeaWorkspace != null) {
			cleanIdeaWorkspace.group = IDE_GROUP_NAME
		}
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
			if (shouldUpdateIdePaths()) {
				updateIdeaSourcePathsAndScopes()
			}
		}
	}

	private void updateIdeaSourcePathsAndScopes() {
		ProjectInfo info = new ProjectInfo(project)

		project.idea {
			module {
				sourceDirs += info.sourceDirs
				testSourceDirs += info.testSourceDirs
				scopes.COMPILE.plus += info.compileConfigurations
				scopes.RUNTIME.plus += info.runtimeConfigurations - info.compileConfigurations
				scopes.TEST.plus += info.testConfigurations

				iml {
					withXml { provider ->
						provider.node.component.content.sourceFolder.each { Node sourceFolder ->
							if (sourceFolder.@url =~ /resources$/) {
								sourceFolder.@type = getSourceFolderTypeString(info, sourceFolder.@url)
							}
						}
					}
				}
			}
		}
	}

	private String getSourceFolderTypeString(ProjectInfo info, String sourceFolderUrl) {
		String partialSrcFolderUrl = sourceFolderUrl - "file://\$MODULE_DIR\$"
		boolean isTestSourceFolder = info.testSourceDirs.find { File testSourceDir ->
			testSourceDir.absolutePath.endsWith(partialSrcFolderUrl)
		}
		isTestSourceFolder ? "java-test-resource" : "java-resource"
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
			if (shouldUpdateIdePaths()) {
				updateEclipseSourcePathAndClassPath()
			}
		}
	}

	private void updateEclipseSourcePathAndClassPath() {
		ProjectInfo info = new ProjectInfo(project)

		project.eclipse {
			classpath {
				plusConfigurations += info.getRuntimeConfigurations() + info.getTestConfigurations()
			}
		}
	}


	private static class ProjectInfo {

		Set<File> sourceDirs
		Set<File> testSourceDirs
		Set<Configuration> compileConfigurations
		Set<Configuration> runtimeConfigurations
		Set<Configuration> testConfigurations

		ProjectInfo(Project project) {
			sourceDirs = []
			testSourceDirs = []
			project.sourceSets.each { SourceSet sourceSet ->
				Set<File> sourceSetSrcDirs = sourceSet.allSource.srcDirs

				if (sourceSet.name =~ /(?i).*test$/) {
					testSourceDirs.addAll(sourceSetSrcDirs)
				} else {
					sourceDirs.addAll(sourceSetSrcDirs)
				}
			}

			compileConfigurations = findConfigurationsMatching(project, /(?i).*(?<!test)compile$/) +
					findConfigurationsMatching(project, /(?i).*(?<!test)compileonly$/)
			runtimeConfigurations = findConfigurationsMatching(project, /(?i).*(?<!test)runtime$/)
			testConfigurations = findConfigurationsMatching(project, /(?i).*testcompile$/) +
					findConfigurationsMatching(project, /(?i).*testcompileonly$/)
		}

		private Set<Configuration> findConfigurationsMatching(Project project, String regex) {
			project.configurations.findAll { Configuration config ->
				config.name =~ regex
			} as Set
		}

	}

}
