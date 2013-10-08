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

import com.bancvue.gradle.categories.ProjectCategory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.ForkOptions
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.GroovyForkOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class ProjectDefaultsPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'project-defaults'

	private Project project
	private ProjectDefaultsProperties defaultsProperties

	public void apply(Project project) {
		this.project = project
		this.defaultsProperties = new ProjectDefaultsProperties(project)
		project.apply(plugin: 'java')
		setDefaultCompileMemorySettings()
		setDefaultTestMemorySettings()
		setCompilerEncoding()
		addBuildDateAndJdkToJarManifest()
		setJavaCompatibilityVersion()
		setDefaultBaseNameForJarTasks()
	}

	private void setDefaultCompileMemorySettings() {
		project.tasks.withType(GroovyCompile) { GroovyCompile compile ->
			GroovyForkOptions forkOptions = compile.groovyOptions.forkOptions
			setCompileMemorySettings(forkOptions)
		}
		project.tasks.withType(JavaCompile) { JavaCompile compile ->
			ForkOptions forkOptions = compile.options.forkOptions
			setCompileMemorySettings(forkOptions)
		}
	}

	private void setCompileMemorySettings(def forkOptions) {
		forkOptions.memoryInitialSize = defaultsProperties.minHeapSize
		forkOptions.memoryMaximumSize = defaultsProperties.maxHeapSize
		forkOptions.jvmArgs << "-XX:MaxPermSize=${defaultsProperties.maxPermSize}".toString()
	}

	private void setDefaultTestMemorySettings() {
		project.tasks.withType(Test) { Test test ->
			test.minHeapSize = defaultsProperties.minTestHeapSize
			test.maxHeapSize = defaultsProperties.maxTestHeapSize
			test.jvmArgs("-XX:MaxPermSize=${defaultsProperties.maxTestPermSize}".toString())
		}
	}

	private void setCompilerEncoding() {
		project.tasks.withType(JavaCompile) { JavaCompile compile ->
			compile.options.encoding = defaultsProperties.compilerEncoding
		}
	}

	private void addBuildDateAndJdkToJarManifest() {
		project.jar {
			manifest {
				attributes 'Built-Date': new Date()
				attributes 'Build-Jdk': System.getProperty('java.version')
			}
		}
	}

	private void setJavaCompatibilityVersion() {
		project.setProperty('sourceCompatibility', defaultsProperties.javaVersion)
		project.setProperty('targetCompatibility', defaultsProperties.javaVersion)
	}

	private void setDefaultBaseNameForJarTasks() {
		project.tasks.withType(Jar) { Jar jar ->
			jar.conventionMapping.baseName = { getDefaultBaseNameForTask(jar) }
		}
	}

	private String getDefaultBaseNameForTask(Jar jar) {
		String baseName = ProjectCategory.getArtifactId(project)
		if (baseName == null) {
			baseName = jar.baseName
		}
		baseName
	}

}
