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
package com.bancvue.gradle.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * Extended by tests which are not testing a plugin itself, but testing classes which are used by plugins and
 * require a project.
 */
class AbstractPluginSupportTest {

	@Rule
	public TemporaryFolder projectDir = new TemporaryFolder()
	protected Project project
	protected ProjectFileSystem projectFS

	@Before
	void setUpProject() {
		project = createProject()
		projectFS = new ProjectFileSystem(project.rootDir)
	}

	protected Project createProject() {
		ProjectBuilder.builder()
			.withName("plugin-support")
			.withProjectDir(projectDir.root)
			.build()
	}

}
