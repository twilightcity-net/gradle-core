package org.dreamscale.gradle.postgres

import org.dreamscale.gradle.test.AbstractPluginIntegrationSpecification
import org.gradle.testkit.runner.BuildResult

class PostgresPluginSpec extends AbstractPluginIntegrationSpecification {

    def "should publish artifact and sources"() {
        given:
        buildFile << """
apply plugin: "org.dreamscale.postgres"
ext.artifactId="test"

postgres {
    dockerContainerName = "postgres-test-container"
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
