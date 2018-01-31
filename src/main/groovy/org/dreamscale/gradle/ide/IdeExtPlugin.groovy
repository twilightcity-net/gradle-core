/*
 * Copyright 2018 DreamScale, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dreamscale.gradle.ide

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class IdeExtPlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = 'org.dreamscale.ide-ext'
    private static final String IDE_GROUP_NAME = 'IDE'

    private Project project

    void apply(Project project) {
        this.project = project

        project.extensions.create(IdeaExtExtension.NAME, IdeaExtExtension)

        project.apply(plugin: 'java')
        applyIdeaPlugin()
        applyEclipsePlugin()
        project.ext['updateIdePaths'] = true
    }


    private boolean shouldUpdateIdePaths() {
        project.ext['updateIdePaths'] == true
    }

    private void applyIdeaPlugin() {
        IdeaProject ideaProject = new IdeaProject(project)

        project.apply(plugin: 'idea')
        addRefreshIdeaTask()
        addRefreshIdeaModuleTask()
        moveCleanIdeaWorkspaceToIdeGroup()
        updateIdeaSourcePathAndScopesAfterProjectEvaluation(ideaProject)

        if (project.idea.project) {
            ideaProject.enableIdeaAnnotationProcessing()
            ideaProject.enableECMAScript6()
            ideaProject.addIdeaGitMappingNodeIfGitRepo()
            ideaProject.configureDefaultConfigurationVmParameters()
        }
    }

    private void updateIdeaSourcePathAndScopesAfterProjectEvaluation(IdeaProject ideaProject) {
        project.afterEvaluate {
            if (shouldUpdateIdePaths()) {
                ideaProject.updateIdeaSourcePathsAndScopesPriorToModuleTaskExecution()
            }
        }
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

}