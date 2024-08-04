/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle.spring


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec

class SpringPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(net.twilightcity.gradle.ide.IdeExtPlugin)
        addLocalSpringProfileToTasksAndIdeaApplicationConfigurations()
    }

    private void addLocalSpringProfileToTasksAndIdeaApplicationConfigurations() {
        project.tasks.withType(JavaExec).configureEach { Task task ->
            systemProperty "spring.profiles.active", "local,${task.name}"
        }
    }

}
