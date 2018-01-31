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
package org.dreamscale.gradle.test

import org.gradle.api.Task

class ComponentTestPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		ComponentTestPlugin.PLUGIN_NAME
	}

	void setup() {
		project.file('src/componentTest').mkdirs()
	}

	def "apply should add componentTest as check dependency"() {
		when:
		applyPlugin()

		then:
		Task checkTask = project.tasks.getByName('check')
		Set checkDependencies = checkTask.getDependsOn()
		checkDependencies.contains(project.tasks.getByName('componentTest'))
	}

}
