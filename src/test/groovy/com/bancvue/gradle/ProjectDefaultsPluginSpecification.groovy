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

import com.bancvue.gradle.test.AbstractPluginSpecification
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile

class ProjectDefaultsPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		ProjectDefaultsPlugin.PLUGIN_NAME
	}

	void setup() {
		project.version = '1.0'
		setArtifactId('bancvue')
	}

	def "apply should apply java plugin and set compatibility"() {
		given:
		project.ext.defaultJavaVersion = '1.8'

		when:
		applyPlugin()

		then:
		assertNamedPluginApplied('java')
		"${project.sourceCompatibility}" == '1.8'
		"${project.targetCompatibility}" == '1.8'
	}

	def "apply should set compiler encoding to Utf8"() {
		when:
		project.apply(plugin: 'groovy')
		applyPlugin()

		then:
		TaskCollection tasks = project.tasks.withType(JavaCompile)
		tasks.size() > 0
		tasks.each { JavaCompile task ->
			assert task.options.encoding == 'UTF-8'
		}

		and:
		TaskCollection groovyTasks = project.tasks.withType(GroovyCompile)
		groovyTasks.size() > 0
		groovyTasks.each { GroovyCompile task ->
			assert task.groovyOptions.encoding == 'UTF-8'
		}
	}

	def "apply should set memory settings for java and groovy compile tasks"() {
		given:
		project.ext.defaultMinHeapSize = '16m'
		project.ext.defaultMaxHeapSize = '24m'
		project.ext.defaultMaxPermSize = '8m'

		when:
		project.apply(plugin: 'groovy')
		applyPlugin()

		then:
		TaskCollection javaTasks = project.tasks.withType(JavaCompile)
		javaTasks.size() > 0
		javaTasks.each { JavaCompile compile ->
			assert compile.options.forkOptions.memoryInitialSize == '16m'
			assert compile.options.forkOptions.memoryMaximumSize == '24m'
			assert compile.options.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}

		and:
		TaskCollection groovyTasks = project.tasks.withType(GroovyCompile)
		groovyTasks.size() > 0
		groovyTasks.each { GroovyCompile compile ->
			assert compile.groovyOptions.forkOptions.memoryInitialSize == '16m'
			assert compile.groovyOptions.forkOptions.memoryMaximumSize == '24m'
			assert compile.groovyOptions.forkOptions.jvmArgs.contains('-XX:MaxPermSize=8m')
		}
	}

	def "apply should set heap size for test tasks"() {
		given:
		project.ext.defaultMinTestHeapSize = '17m'
		project.ext.defaultMaxTestHeapSize = '23m'
		project.ext.defaultMaxTestPermSize = '5m'

		when:
		applyPlugin()

		then:
		TaskCollection tasks = project.tasks.withType(org.gradle.api.tasks.testing.Test)
		tasks.size() > 0
		tasks.each { org.gradle.api.tasks.testing.Test test ->
			assert test.minHeapSize == '17m'
			assert test.maxHeapSize == '23m'
			assert test.jvmArgs.contains('-XX:MaxPermSize=5m')
		}
	}

	def "apply should set java tmp dir"() {
		given:
		String originalTmpDir = System.getProperty("java.io.tmpdir")
		System.setProperty("java.io.tmpdir", "new_tmpdir_value")

		when:
		project.apply(plugin: 'groovy')
		applyPlugin()

		then:
		TaskCollection testTasks = project.tasks.withType(org.gradle.api.tasks.testing.Test)
		testTasks.size() > 0
		testTasks.each { org.gradle.api.tasks.testing.Test test ->
			assert test.systemProperties["java.io.tmpdir"] == "new_tmpdir_value"
		}

		and:
		TaskCollection javaTasks = project.tasks.withType(JavaCompile)
		javaTasks.size() > 0
		javaTasks.each { JavaCompile compile ->
			assert compile.options.forkOptions.jvmArgs.contains("-Djava.io.tmpdir=new_tmpdir_value")
		}

		and:
		TaskCollection groovyTasks = project.tasks.withType(GroovyCompile)
		groovyTasks.size() > 0
		groovyTasks.each { GroovyCompile compile ->
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

		then:
		plugin.getDefaultBaseNameForTask(jarTask) == 'someName'
	}

	def "getDefaultBaseNameForTask should use artifactId if project artifactId defined"() {
		when:
		applyPlugin()
		Jar jarTask = project.tasks.create('jarTask', Jar)
		setArtifactId('some-artifact')

		then:
		ProjectDefaultsPlugin plugin = getPlugin()
		plugin.getDefaultBaseNameForTask(jarTask) == 'some-artifact'
	}

}
