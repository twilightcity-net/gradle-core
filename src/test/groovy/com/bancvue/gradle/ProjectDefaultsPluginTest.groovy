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

import com.bancvue.gradle.test.AbstractPluginTest
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.junit.Before
import org.junit.Test

class ProjectDefaultsPluginTest extends AbstractPluginTest {

	ProjectDefaultsPluginTest() {
		super(ProjectDefaultsPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		project.version = '1.0'
		setArtifactId('bancvue')
	}

	@Test
	void apply_ShouldSetGroupNameToBancvue() {
		applyPlugin()

		project.group == 'com.bancvue'
	}

	@Test
	void apply_ShouldApplyJavaPluginAndSetCompatibility() {
		project.ext.defaultJavaVersion = '1.8'

		applyPlugin()

		assertNamedPluginApplied('java')
		assert "${project.sourceCompatibility}" == '1.8'
		assert "${project.targetCompatibility}" == '1.8'
	}

	@Test
	void apply_ShouldSetCompilerEncodingToUtf8() {
		applyPlugin()

		TaskCollection tasks = project.tasks.withType(JavaCompile)
		assert tasks.size() > 0
		tasks.each { JavaCompile task ->
			assert task.options.encoding == 'UTF-8'
		}
	}

	@Test
	void apply_ShouldSetMemorySettingsForJavaAndGroovyCompileTasks() {
		project.ext.defaultMinHeapSize = '16m'
		project.ext.defaultMaxHeapSize = '24m'
		project.ext.defaultMaxPermSize = '8m'

		project.apply(plugin: 'groovy')
		applyPlugin()

		TaskCollection javaTasks = project.tasks.withType(JavaCompile)
		assert javaTasks.size() > 0
		javaTasks.each { JavaCompile compile ->
			assert compile.options.forkOptions.memoryInitialSize == '16m'
			assert compile.options.forkOptions.memoryMaximumSize == '24m'
			assert compile.options.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}

		TaskCollection groovyTasks = project.tasks.withType(GroovyCompile)
		assert groovyTasks.size() > 0
		groovyTasks.each { GroovyCompile compile ->
			assert compile.groovyOptions.forkOptions.memoryInitialSize == '16m'
			assert compile.groovyOptions.forkOptions.memoryMaximumSize == '24m'
			assert compile.groovyOptions.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}
	}

	@Test
	void apply_ShouldSetHeapSizeForTestTasks() {
		project.ext.defaultMinTestHeapSize = '17m'
		project.ext.defaultMaxTestHeapSize = '23m'
		project.ext.defaultMaxTestPermSize = '5m'

		applyPlugin()

		TaskCollection tasks = project.tasks.withType(org.gradle.api.tasks.testing.Test)
		assert tasks.size() > 0
		tasks.each { org.gradle.api.tasks.testing.Test test ->
			assert test.minHeapSize == '17m'
			assert test.maxHeapSize == '23m'
			assert test.jvmArgs.contains('-XX:MaxPermSize=5m')
		}
	}

	@Test
	void apply_ShouldAddBuildDateAndJdkToJarManifest() {
		String expectedJavaVersion = System.getProperty('java.version')

		applyPlugin()

		assert project.jar.manifest.attributes['Built-Date'] != null
		assert project.jar.manifest.attributes['Build-Jdk'] == expectedJavaVersion
	}

	@Test
	void getDefaultBaseNameForTask_ShouldUseTaskBaseName_IfProjectArtifactIdNotDefined() {
		project = createProject() // re-create project since artifactId is set as part of setUp
		Jar jarTask = project.tasks.create('jarTask', Jar)
		jarTask.baseName = 'someName'

		applyPlugin()
		ProjectDefaultsPlugin plugin = getPlugin()
		plugin.project = project
		String baseName = plugin.getDefaultBaseNameForTask(jarTask)

		assert baseName == 'someName'
	}

	@Test
	void getDefaultBaseNameForTask_ShouldUseArtifactId_IfProjectArtifactIdDefined() {
		applyPlugin()
		Jar jarTask = project.tasks.create('jarTask', Jar)
		setArtifactId('some-artifact')

		ProjectDefaultsPlugin plugin = getPlugin()
		String baseName = plugin.getDefaultBaseNameForTask(jarTask)

		assert baseName == 'some-artifact'
	}

}
