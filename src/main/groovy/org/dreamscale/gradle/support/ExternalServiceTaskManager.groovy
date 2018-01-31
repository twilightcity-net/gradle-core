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
package org.dreamscale.gradle.support

import org.gradle.api.Project
import org.gradle.api.Task


class ExternalServiceTaskManager {

    private Project project
    private Closure dependentTaskResolver
    private Task initExternalServiceTask

    ExternalServiceTaskManager(Project project, String serviceName, Closure dependentTaskResolver) {
        this.project = project
        this.dependentTaskResolver = dependentTaskResolver
        this.initExternalServiceTask = project.tasks.create("init${serviceName.capitalize()}")

        project.afterEvaluate {
            serviceDependentTasks.each { Task task ->
                task.dependsOn initExternalServiceTask
            }
        }
    }

    void addInitializationTask(Task initTask) {
        initExternalServiceTask.dependsOn initTask
    }

    List<Task> getServiceDependentTasks() {
        List<String> dependentTaskNames = dependentTaskResolver()
        dependentTaskNames.findResults { String taskName ->
            project.tasks.findByName(taskName)
        }
    }

}
