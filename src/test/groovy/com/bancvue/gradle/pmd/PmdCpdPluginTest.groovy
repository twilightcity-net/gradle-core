package com.bancvue.gradle.pmd

import com.bancvue.gradle.AbstractPluginTest
import com.google.common.io.Files
import org.junit.Before
import org.junit.Test

class PmdCpdPluginTest extends AbstractPluginTest {

	PmdCpdPluginTest() {
		super(PmdCpdPlugin.PLUGIN_NAME)
	}

	@Before
	void setup() {
		project.apply(plugin: 'java')
		project.apply(plugin: PmdCpdPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldConfigureTaskFromExtension() {
		project.sourceSets {
			main
		}

		CpdExtension extension = new CpdExtension()
		int expectedMinimumTokenCount = extension.minimumTokenCount + 10
		boolean expectedIgnoreLiterals = !extension.ignoreLiterals
		boolean expectedIgnoreIdentifiers = !extension.ignoreIdentifiers
		boolean expectedIgnoreFailures = !extension.ignoreFailures
		File expectedReportsDir = Files.createTempDir()
		project.cpd {
			ignoreFailures = expectedIgnoreFailures
			minimumTokenCount = expectedMinimumTokenCount
			ignoreLiterals = expectedIgnoreLiterals
			ignoreIdentifiers = expectedIgnoreIdentifiers
			reportsDir = expectedReportsDir
		}

		project.tasks.withType(Cpd) { Cpd task ->
			assert task != null
			assert task.ignoreFailures == expectedIgnoreFailures
			assert task.ignoreLiterals == expectedIgnoreLiterals
			assert task.ignoreIdentifiers == expectedIgnoreIdentifiers
			assert task.minimumTokenCount == expectedMinimumTokenCount
			assert task.reportDir == expectedReportsDir
		}
	}

	@Test
	void apply_ShouldUseExtensionDefaults_IfValuesNotProvidedInTask() {
		project.sourceSets {
			main
		}

		CpdExtension extension = new CpdExtension()
		project.tasks.withType(Cpd) { Cpd task ->
			assert task != null
			assert task.ignoreFailures == extension.ignoreIdentifiers
			assert task.ignoreLiterals == extension.ignoreLiterals
			assert task.ignoreIdentifiers == extension.ignoreIdentifiers
			assert task.minimumTokenCount == extension.minimumTokenCount
			assert task.reportDir == new File(project.buildDir, 'reports/cpd')
		}
	}

}
