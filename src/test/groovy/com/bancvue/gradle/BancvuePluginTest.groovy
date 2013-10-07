/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle

import com.bancvue.gradle.maven.MavenExtPlugin
import com.bancvue.gradle.support.ProjectSupportPlugin
import com.bancvue.gradle.test.AbstractPluginTest
import com.bancvue.gradle.test.ComponentTestPlugin
import com.bancvue.gradle.test.TestExtPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class BancvuePluginTest extends AbstractPluginTest {

	@Rule
	public ExpectedException exception = ExpectedException.none()

	BancvuePluginTest() {
		super(BancvueOssPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		project.version = '1.0'
		setArtifactId('bancvue')
	}

	@Test
	void apply_ShouldApplyGroovyPlugin() {
		applyPlugin()

		assertNamedPluginApplied('groovy')
	}

	@Test
	void apply_ShouldFail_IfVersionNotDefined() {
		exception.expect(BancvueOssPlugin.VersionNotDefinedException)
		project = ProjectBuilder.builder().withName('project').build()
		setArtifactId('bancvue')

		applyPlugin()
	}

	@Test
	void apply_ShouldFail_IfArtifactIdNotDefined() {
		exception.expect(BancvueOssPlugin.ArtifactIdNotDefinedException)
		project = ProjectBuilder.builder().withName('project').build()
		project.version = '1.0'

		applyPlugin()
	}

	@Test
	void apply_ShouldApplyBancvueIdePlugin() {
		applyPlugin()

		assertNamedPluginApplied(IdeExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvuePublishPlugin() {
		applyPlugin()

		assertNamedPluginApplied(MavenExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyTestExtPlugin() {
		applyPlugin()

		assertNamedPluginApplied(TestExtPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueComponentTestPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ComponentTestPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueUtilitiesPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ProjectSupportPlugin.PLUGIN_NAME)
	}

	@Test
	void apply_ShouldApplyBancvueDefaultsPlugin() {
		applyPlugin()

		assertNamedPluginApplied(ProjectDefaultsPlugin.PLUGIN_NAME)
	}

}
