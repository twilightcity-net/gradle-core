/*
 * Copyright 2014 TwilightCity, Inc
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


import org.gradle.testkit.runner.BuildResult

class PostgresPluginSpec extends net.twilightcity.gradle.test.AbstractPluginIntegrationSpecification {

    def "should publish artifact and sources"() {
        given:
        buildFile << """
apply plugin: "net.twilightcity.postgres"
ext.artifactId="test"

postgres {
    dockerContainerName = "postgres-test-container"
    postgresPort = "5433"
}
		"""

        when:
        BuildResult result = run("removePostgresTestContainer", "startPostgresTestContainer", "createApplicationDatabase")

        then:
        assert result.output.contains("Creating database test")

        when:
        result = run("createApplicationDatabase")

        then:
        assert result.output.contains("Database test already exists")

        cleanup:
        run("removePostgresTestContainer")
    }

}
