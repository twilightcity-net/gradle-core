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
package com.bancvue.gradle.support

import com.bancvue.gradle.test.AbstractPluginSpecification

class ProjectSupportPluginSpecification extends AbstractPluginSpecification {


	String getPluginName() {
		ProjectSupportPlugin.PLUGIN_NAME
	}

	void setup() {
		setArtifactId('1.0')
	}

	def "apply should add printClasspath task"() {
		given:
		assert project.tasks.withType(PrintClasspath).isEmpty()

		when:
		applyPlugin()

		then:
		!project.tasks.withType(PrintClasspath).isEmpty()
	}

	def "apply should add clearGroupCache task"() {
		given:
		assert project.tasks.findByName('clearGroupCache') == null

		when:
		applyPlugin()

		then:
		project.tasks.findByName('clearGroupCache') instanceof ClearArtifactCache
	}

}
