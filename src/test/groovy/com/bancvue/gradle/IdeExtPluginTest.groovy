package com.bancvue.gradle

import org.junit.Test

class IdeExtPluginTest extends AbstractPluginTest {

	IdeExtPluginTest() {
		super(IdeExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyIdeaPlugin() {
		applyPlugin()

		assertNamedPluginApplied('idea')
	}

	@Test
	void apply_ShouldApplyEclipsePlugin() {
		applyPlugin()

		assertNamedPluginApplied('eclipse')
	}

}
