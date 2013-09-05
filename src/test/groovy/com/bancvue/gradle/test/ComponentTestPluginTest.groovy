package com.bancvue.gradle.test

import com.bancvue.gradle.AbstractPluginTest
import org.gradle.api.Task
import org.junit.Before
import org.junit.Test

class ComponentTestPluginTest extends AbstractPluginTest {

	ComponentTestPluginTest() {
		super(ComponentTestPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		project.file('src/componentTest').mkdirs()
	}

	@Test
	void apply_ShouldAddComponentTestAsCheckDependency() {
		applyPlugin()

		Task checkTask = project.tasks.getByName('check')
		Set checkDependencies = checkTask.getDependsOn()
		assert checkDependencies.contains(project.tasks.getByName('componentTest'))
	}

}
