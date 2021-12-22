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
package net.twilightcity.gradle.test

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

abstract class AbstractPluginSpecification extends AbstractProjectSpecification {

    String getProjectName() {
        pluginName
    }

    abstract String getPluginName()

    protected Project getProject() {
        super.project
    }

    protected void applyPlugin() {
        project.apply(plugin: pluginName)
    }

    protected Plugin getPlugin() {
        project.plugins.getPlugin(pluginName)
    }

    protected Plugin getNamedPlugin(String pluginName) {
        project.plugins.getPlugin(pluginName)
    }

    protected void assertNamedPluginApplied(String pluginName) {
        assert getNamedPlugin(pluginName) != null
    }

    protected List<String> getDependencyNamesForTask(String taskName) {
        Task task = project.tasks.getByName(taskName)
        task.taskDependencies.getDependencies(task).toList().collect { it.name }
    }

    protected void assertTaskDependency(String taskName, String... expectedDependencyNames) {
        expectedDependencyNames.each { String expectedDependencyName ->
            assertTaskDependency(taskName, expectedDependencyName)
        }
    }

    protected void assertTaskDependency(String taskName, String expectedDependencyName) {
        List<String> dependencyNames = getDependencyNamesForTask(taskName)
        assert dependencyNames.contains(expectedDependencyName)
        "Expected task ${taskName} to declare dependency on ${expectedDependencyName}, actual dependencies: ${dependencyNames}"
    }

    protected void assertNoTaskDependency(String taskName, String expectedMissingDependencyName) {
        List<String> dependencyNames = getDependencyNamesForTask(taskName)
        assert !dependencyNames.contains(expectedMissingDependencyName)
        "Expected task ${taskName} to NOT declare dependency on ${expectedMissingDependencyName}, actual dependencies: ${dependencyNames}"
    }

    protected boolean assertTasksDefined(String... names) {
        names.each { String name ->
            assert project.tasks.findByName(name)
        }
    }

    protected boolean assertTasksNotDefined(String... names) {
        names.each { String name ->
            assert !project.tasks.findByName(name)
        }
    }
}
