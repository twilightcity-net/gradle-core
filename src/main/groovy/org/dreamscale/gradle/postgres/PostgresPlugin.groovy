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
package org.dreamscale.gradle.postgres

import org.dreamscale.gradle.ResourceResolver
import org.dreamscale.gradle.support.ExternalServiceTaskManager
import org.dreamscale.gradle.docker.DockerPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec

class PostgresPlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = "postgres"

    private Project project
    private ExternalServiceTaskManager externalServiceTaskManager

    void apply(Project project) {
        this.project = project

        PostgresExtension extension = project.extensions.create(PostgresExtension.NAME, PostgresExtension)

        externalServiceTaskManager = new ExternalServiceTaskManager(project, "postgres", { extension.dependentTaskNames })

        createPostgresDockerContainer()
        Task waitForPostgresTask = createWaitForPostgresInitializationTask()
        Task injectCreateDatabaseScript = createInjectCreateDatabaseScriptIntoPostgresContainerTask(waitForPostgresTask)
        createCreateApplicationDatabaseTask(injectCreateDatabaseScript, true)
        createCreateApplicationDatabaseTask(injectCreateDatabaseScript, false)
    }

    private PostgresExtension getPluginExtension() {
        project.extensions.getByName(PostgresExtension.NAME) as PostgresExtension
    }

    private void createPostgresDockerContainer() {
        project.apply(plugin: DockerPlugin.PLUGIN_NAME)

        project.afterEvaluate {
            project.dockerContainers {
                container {
                    name pluginExtension.dockerContainerName
                    imageName pluginExtension.dockerImageName
                    publish "${pluginExtension.postgresPort}:5432"
                    env "POSTGRES_USER=${pluginExtension.postgresUsername}"
                    env "POSTGRES_PASSWORD=${pluginExtension.postgresPassword}"
                }
            }
        }
    }

    private void createCreateApplicationDatabaseTask(Task injectCreateDatabaseScript, boolean testDatabase) {
        String taskName = "create${testDatabase ? "Test" : "Application"}Database"

        Task createAppDb = project.tasks.create(name: taskName, type: Exec, dependsOn: injectCreateDatabaseScript) {
            doFirst {
                String database = pluginExtension.applicationDatabaseName
                if (database == null) {
                    database = project.ext.artifactId
                }

                database = "${database}${testDatabase ? "-test" : ""}"
                println "Creating Postgres database '${database}'"
                commandLine 'docker', 'exec', pluginExtension.dockerContainerName, 'create_database_if_not_created.sh', pluginExtension.postgresUsername, database
            }
        }

        project.afterEvaluate {
            List dependentTasks = testDatabase ? pluginExtension.dependentTestTaskNames : pluginExtension.dependentTaskNames
            for (String dependentTaskName : dependentTasks) {
                project.tasks.findByName(dependentTaskName)?.dependsOn createAppDb
            }
        }
    }

    private Task createInjectCreateDatabaseScriptIntoPostgresContainerTask(Task waitForPostgresInitializationTask) {
        File createDatabaseScript = new File("${project.buildDir}/postgres/create_database_if_not_created.sh")
        Task injectCreateDatabaseScript = project.tasks.create(name: "injectCreateDatabaseScriptIntoPostgresContainer", type: Exec, dependsOn: waitForPostgresInitializationTask) {
            doFirst {
                ResourceResolver.create(project).extractResourceToFile("postgres/create_database_if_not_created.sh", createDatabaseScript, true)
            }
            commandLine "docker", "cp", createDatabaseScript.absolutePath, "${pluginExtension.dockerContainerName}:/usr/local/bin"
        }

        externalServiceTaskManager.addInitializationTask(injectCreateDatabaseScript)
        injectCreateDatabaseScript
    }

    private Task createWaitForPostgresInitializationTask() {
        File waitForPostgresScript = new File("${project.buildDir}/postgres/wait_for_postgres.sh")
        Task waitForPostgres = project.tasks.create(name: "waitForPostgres", type: Exec) {
            doFirst {
                ResourceResolver.create(project).extractResourceToFile("postgres/wait_for_postgres.sh", waitForPostgresScript, true)
            }
            commandLine waitForPostgresScript.absolutePath
        }

        project.afterEvaluate {
            String startPostgresTaskName = (project.hasProperty("postgres.refreshContainer") ? "refresh" : "start") + pluginExtension.dockerContainerName.capitalize()
            waitForPostgres.dependsOn project.tasks.getByName(startPostgresTaskName)
        }

        externalServiceTaskManager.addInitializationTask(waitForPostgres)
        waitForPostgres
    }

}