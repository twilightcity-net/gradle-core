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
package net.twilightcity.gradle.ide

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

class IdeExtPlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = 'net.twilightcity.ide-ext'
    private static final String IDE_GROUP_NAME = 'IDE'

    private Project project

    void apply(Project project) {
        this.project = project

        project.extensions.create(IdeaExtExtension.NAME, IdeaExtExtension)

        project.pluginManager.apply(JavaPlugin)
        applyIdeaPlugin()
        applyEclipsePlugin()
        project.ext['updateIdePaths'] = true
    }


    private boolean shouldUpdateIdePaths() {
        project.ext['updateIdePaths'] == true
    }

    private void applyIdeaPlugin() {
        IdeaProject ideaProject = new IdeaProject(project)

        project.pluginManager.apply(IdeaPlugin)
        addRefreshIdeaTask()
        addRefreshIdeaModuleTask()
        repairCleanIdeaWorkspaceTask()
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

    /**
     * For whatever reason, idea executes ideaWorkspace but cleanIdea does not execute cleanIdeaWorkspace.
     * Also, no task depends on cleanIdeaWorkspace and it's getting listed in group 'Other', so
     * move it to the IDE group.
     */
    private void repairCleanIdeaWorkspaceTask() {
        Task cleanIdeaWorkspace = project.tasks.findByName('cleanIdeaWorkspace')
        // workspace tasks are only available on the root project
        if (cleanIdeaWorkspace != null) {
            Task cleanIdea = project.tasks.getByName('cleanIdea')
            cleanIdea.dependsOn(cleanIdeaWorkspace)
            cleanIdeaWorkspace.group = IDE_GROUP_NAME
        }
    }


    private void applyEclipsePlugin() {
        project.pluginManager.apply(EclipsePlugin)
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
