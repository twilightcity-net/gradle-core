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
package net.twilightcity.gradle

import net.twilightcity.gradle.test.AbstractPluginSpecification
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.BaseForkOptions
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class ProjectDefaultsPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		ProjectDefaultsPlugin.PLUGIN_NAME
	}

	void setup() {
		project.version = '1.0'
		setArtifactId('twilightcity')
	}

	def "apply should apply java plugin and set compatibility"() {
		given:
		project.ext.defaultJavaVersion = '1.8'

		when:
		applyPlugin()
		evaluateProject()

		then:
		assertNamedPluginApplied('java')
		"${project.sourceCompatibility}" == '1.8'
		"${project.targetCompatibility}" == '1.8'
	}

	def "apply should set compiler encoding to Utf8"() {
		when:
		project.apply(plugin: 'groovy')
		applyPlugin()
		evaluateProject()

		then:
		assertSettingsAppliedToTasks(JavaCompile) { JavaCompile task ->
			assert task.options.encoding == 'UTF-8'
		}

		and:
		assertSettingsAppliedToTasks(GroovyCompile) { GroovyCompile task ->
			assert task.groovyOptions.encoding == 'UTF-8'
		}
	}

	def "apply should set memory settings for java and groovy compile tasks"() {
		given:
		project.ext.defaultMinHeapSize = '16m'
		project.ext.defaultMaxHeapSize = '24m'

		when:
		project.apply(plugin: 'groovy')
		applyPlugin()
		evaluateProject()

		then:
		assertSettingsAppliedToTasks(JavaCompile) { JavaCompile compile ->
			assertForkOptions(compile.options.forkOptions, '16m', '24m')
		}

		and:
		assertSettingsAppliedToTasks(GroovyCompile) { GroovyCompile compile ->
			assertForkOptions(compile.groovyOptions.forkOptions, '16m', '24m')
		}
	}

	private void assertSettingsAppliedToTasks(Class type, Closure assertionClosure) {
		TaskCollection tasks = project.tasks.withType(type)
		assert tasks.size() > 0
		tasks.each assertionClosure
	}

	private void assertForkOptions(BaseForkOptions forkOptions, String memoryInitial, String memoryMaximum) {
		assert forkOptions.memoryInitialSize == memoryInitial
		assert forkOptions.memoryMaximumSize == memoryMaximum
	}

	def "apply should set memory settings for test tasks"() {
		given:
		project.ext.defaultMinTestHeapSize = '17m'
		project.ext.defaultMaxTestHeapSize = '23m'

		when:
		applyPlugin()
		evaluateProject()

		then:
		assertSettingsAppliedToTasks(Test) { Test test ->
			assert test.minHeapSize == '17m'
			assert test.maxHeapSize == '23m'
		}
	}

	def "apply should set java tmp dir"() {
		given:
		String originalTmpDir = System.getProperty("java.io.tmpdir")
		System.setProperty("java.io.tmpdir", "new_tmpdir_value")

		when:
		project.apply(plugin: 'groovy')
		applyPlugin()
		evaluateProject()

		then:
		assertSettingsAppliedToTasks(Test) { Test test ->
			assert test.allJvmArgs.contains("-Djava.io.tmpdir=new_tmpdir_value")
		}

		and:
		assertSettingsAppliedToTasks(JavaCompile) { JavaCompile compile ->
			assert compile.options.forkOptions.jvmArgs.contains("-Djava.io.tmpdir=new_tmpdir_value")
		}

		and:
		assertSettingsAppliedToTasks(GroovyCompile) { GroovyCompile compile ->
			assert compile.groovyOptions.forkOptions.jvmArgs.contains("-Djava.io.tmpdir=new_tmpdir_value")
		}

		cleanup:
		System.setProperty("java.io.tmpdir", originalTmpDir)
	}

	def "apply should set default jar baseName and allow override"() {
		given:
		Jar jarTask = project.tasks.create('jarTask', Jar)
		setArtifactId('some-artifact')

		when:
		applyPlugin()
		evaluateProject()

		then:
		jarTask.baseName == 'some-artifact'

		when:
		jarTask.baseName = 'other-artifact'

		then:
		jarTask.baseName == 'other-artifact'
	}

	def "getDefaultBaseNameForTask should use task baseName if project artifactId not defined"() {
		given:
		project = createProject() // re-create project since artifactId is set as part of setUp
		Jar jarTask = project.tasks.create('jarTask', Jar)
		jarTask.baseName = 'someName'

		when:
		applyPlugin()
		ProjectDefaultsPlugin plugin = getPlugin()
		plugin.project = project
		evaluateProject()

		then:
		plugin.getDefaultBaseNameForTask(jarTask) == 'someName'
	}

	def "getDefaultBaseNameForTask should use artifactId if project artifactId defined"() {
		when:
		applyPlugin()
		Jar jarTask = project.tasks.create('jarTask', Jar)
		setArtifactId('some-artifact')
		evaluateProject()

		then:
		ProjectDefaultsPlugin plugin = getPlugin()
		plugin.getDefaultBaseNameForTask(jarTask) == 'some-artifact'
	}

	def "should allow settings to be configured after plugin has been applied"() {
		given:
		project.apply(plugin: 'groovy')
		applyPlugin()

		when:
		project.ext.defaultJavaVersion = '1.5'
		project.ext.defaultMinHeapSize = '1m'
		project.ext.defaultMaxHeapSize = '2m'
		project.ext.defaultMinTestHeapSize = '4m'
		project.ext.defaultMaxTestHeapSize = '5m'
		project.ext.defaultCompilerEncoding = 'ASCII'
		evaluateProject()

		then:
		"${project.sourceCompatibility}" == '1.5'
		"${project.targetCompatibility}" == '1.5'

		and:
		assertSettingsAppliedToTasks(JavaCompile) { JavaCompile compile ->
			compile.options.encoding = 'ASCII'
			assertForkOptions(compile.options.forkOptions, '1m', '2m')
		}

		and:
		assertSettingsAppliedToTasks(GroovyCompile) { GroovyCompile compile ->
			compile.groovyOptions.encoding = 'ASCII'
			assertForkOptions(compile.groovyOptions.forkOptions, '1m', '2m')
		}

		and:
		assertSettingsAppliedToTasks(Test) { Test test ->
			assert test.minHeapSize == '4m'
			assert test.maxHeapSize == '5m'
		}
	}

	def "should respect memory settings applied directly to tasks after plugin has been applied"() {
		given:
		project.apply(plugin: 'groovy')
		applyPlugin()

		when:
		project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.encoding = 'java-encoding'
			compile.options.forkOptions.memoryInitialSize = '1m'
			compile.options.forkOptions.memoryMaximumSize = '2m'
		}
		project.tasks.withType(GroovyCompile) { GroovyCompile compile ->
			compile.groovyOptions.encoding = 'groovy-encoding'
			compile.groovyOptions.forkOptions.memoryInitialSize = '4m'
			compile.groovyOptions.forkOptions.memoryMaximumSize = '5m'
		}
		project.tasks.withType(Test) { Test test ->
			test.minHeapSize = '7m'
			test.maxHeapSize = '8m'
		}
		evaluateProject()

		then:
		assertSettingsAppliedToTasks(JavaCompile) { JavaCompile compile ->
			assert compile.options.encoding == 'java-encoding'
			assertForkOptions(compile.options.forkOptions, '1m', '2m')
		}

		and:
		assertSettingsAppliedToTasks(GroovyCompile) { GroovyCompile compile ->
			assert compile.groovyOptions.encoding == 'groovy-encoding'
			assertForkOptions(compile.groovyOptions.forkOptions, '4m', '5m')
		}

		and:
		assertSettingsAppliedToTasks(Test) { Test test ->
			assert test.minHeapSize == '7m'
			assert test.maxHeapSize == '8m'
		}
	}

}
