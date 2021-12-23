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
package net.twilightcity.gradle.postgres

class PostgresExtension {

    static final String NAME = "postgres"

    String dockerImageName = "postgres:9.6"
    String dockerContainerName = "postgres"

    String postgresPort = "5432"
    String postgresUsername = "postgres"
    String postgresPassword = "postgres"

    List<String> dependentTaskNames = ["bootRun"]
    List<String> dependentTestTaskNames = ["componentTest", "integrationTest"]

    String applicationDatabaseName

    String getTaskSuffix() {
        dockerContainerName.replaceAll("(_|-)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() }).capitalize()
    }

}
