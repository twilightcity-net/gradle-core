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
package net.twilightcity.gradle.mongodb

import org.betterdevxp.dockerdsl.DockerDslPlugin
import net.twilightcity.gradle.ide.IdeExtPlugin
import net.twilightcity.gradle.support.ExternalServiceTaskManager
import org.gradle.api.Plugin
import org.gradle.api.Project

class MongoPlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = "mongo"

    private Project project
    private ExternalServiceTaskManager externalServiceTaskManager

    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(IdeExtPlugin)

        MongoExtension extension = project.extensions.create(MongoExtension.NAME, MongoExtension)

        externalServiceTaskManager = new ExternalServiceTaskManager(project, "mongo", { extension.dependentTaskNames })

        createMongoDockerContainer()

        project.afterEvaluate {
            externalServiceTaskManager.addExternalServiceInitializationTask(project.tasks.findByName("startMongo"))
        }
    }

    private MongoExtension getPluginExtension() {
        project.extensions.getByName(MongoExtension.NAME) as MongoExtension
    }

    private void createMongoDockerContainer() {
        project.pluginManager.apply(DockerDslPlugin)

        project.afterEvaluate {
            project.docker {
                container {
                    name pluginExtension.dockerContainerName
                    imageName pluginExtension.dockerImageName
                    publish "${pluginExtension.mongoPort}:27017"
                    env "MONGODB_USERNAME=${pluginExtension.mongoUsername}"
                    env "MONGODB_PASSWORD=${pluginExtension.mongoPassword}"
                }
            }
        }
    }
}
