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
import com.bancvue.gradle.test.ComponentTestPlugin
import com.bancvue.gradle.test.JacocoExtPlugin
import com.bancvue.gradle.test.TestExtPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class BancvueOssPlugin implements Plugin<Project> {

	static class VersionNotDefinedException extends GradleException {
		public VersionNotDefinedException() {
			super('Required property "version" must be specified')
		}
	}

	static class ArtifactIdNotDefinedException extends GradleException {
		public ArtifactIdNotDefinedException() {
			super('Required property "artifactId" must be specified')
		}
	}

	static final String PLUGIN_NAME = 'bancvue-oss'

	private Project project

	public void apply(Project project) {
		this.project = project
		project.group = 'com.bancvue'
		assertRequiredPropertiesDefined()
		applyJavaPlugin()
		applyGroovyPlugin()
		applyBancvueDefaultsPlugin()
		applyMavenPublishExtPlugin()
		applyTestExtPlugin()
		applyComponentTestPlugin()
		applyJacocoExtPlugin()
		applyIdeExtPlugin()
		applyBancvueUtilPlugin()
	}

	private void assertRequiredPropertiesDefined() {
		assertVersionDefined()
		assertArtifactIdDefined()
	}

	private void assertVersionDefined() {
		if (!project.version || (project.version == Project.DEFAULT_VERSION)) {
			throw new VersionNotDefinedException()
		}
	}

	private void assertArtifactIdDefined() {
		if (!project.ext.properties.containsKey('artifactId')) {
			throw new ArtifactIdNotDefinedException()
		}
	}

	private void applyJavaPlugin() {
		project.apply(plugin: 'java')
	}

	private void applyGroovyPlugin() {
		project.apply(plugin: 'groovy')
	}

	private void applyIdeExtPlugin() {
		project.apply(plugin: IdeExtPlugin.PLUGIN_NAME)
	}

	private void applyMavenPublishExtPlugin() {
		project.apply(plugin: MavenExtPlugin.PLUGIN_NAME)
	}

	private void applyTestExtPlugin() {
		project.apply(plugin: TestExtPlugin.PLUGIN_NAME)
	}

	private void applyComponentTestPlugin() {
		project.apply(plugin: ComponentTestPlugin.PLUGIN_NAME)
	}

	private void applyJacocoExtPlugin() {
		project.apply(plugin: JacocoExtPlugin.PLUGIN_NAME)
	}

	private void applyBancvueUtilPlugin() {
		project.apply(plugin: ProjectSupportPlugin.PLUGIN_NAME)
	}

	private void applyBancvueDefaultsPlugin() {
		project.apply(plugin: ProjectDefaultsPlugin.PLUGIN_NAME)
	}

}
