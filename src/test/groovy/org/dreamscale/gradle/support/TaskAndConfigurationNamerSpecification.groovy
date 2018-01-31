/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dreamscale.gradle.support

import spock.lang.Specification

class TaskAndConfigurationNamerSpecification extends Specification {

	private TaskAndConfigurationNamer resolver

	def "getSourceSetNameAppendix should return empty string if id is main"() {
		when:
		resolver = new TaskAndConfigurationNamer("main")

		then:
		assert resolver.getSourceSetNameAppendix() == ""
	}

	def "getSourceSetNameAppendix should strip main if id starts with main"() {
		when:
		resolver = new TaskAndConfigurationNamer("mainTest")

		then:
		resolver.getSourceSetNameAppendix() == "test"
	}

	def "getSourceSetNameAppendix should return publication id if id does not start with main"() {
		when:
		resolver = new TaskAndConfigurationNamer("isnotmain")

		then:
		resolver.getSourceSetNameAppendix() == "isnotmain"
	}

	def "getArtifactIdAppendix should de-camel case publication id"() {
		when:
		resolver = new TaskAndConfigurationNamer("idWithCamelCase")

		then:
		resolver.getArtifactIdAppendix() == "id-with-camel-case"
	}

	def "createJarTaskName should use base name if publication id is main"() {
		when:
		resolver = new TaskAndConfigurationNamer("main")

		then:
		resolver.getJarTaskName() == "jar"
	}

	def "createJarTaskName should append publication id to base name if base name is not main"() {
		when:
		resolver = new TaskAndConfigurationNamer("mainTest")

		then:
		resolver.getJarTaskName() == "jarMainTest"
	}

}
