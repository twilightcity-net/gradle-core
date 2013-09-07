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

import com.bancvue.gradle.tasks.ClearArtifactCache
import com.bancvue.gradle.tasks.PrintClasspath
import org.junit.Before
import org.junit.Test

class ProjectSupportPluginTest extends AbstractPluginTest {


	ProjectSupportPluginTest() {
		super(ProjectSupportPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		setArtifactId('1.0')
	}

	@Test
	void apply_ShouldAddPrintClasspathTask() {
		assert project.tasks.withType(PrintClasspath).isEmpty()

		applyPlugin()

		assert !project.tasks.withType(PrintClasspath).isEmpty()
	}

	@Test
	void apply_ShouldAddClearGroupCacheTask() {
		assert project.tasks.findByName('clearGroupCache') == null

		applyPlugin()

		assert project.tasks.findByName('clearGroupCache') instanceof ClearArtifactCache
	}

}
