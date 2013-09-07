/*
 * Copyright 2013 BancVue, LTD
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package com.bancvue.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Project

@Slf4j
class DefaultProjectPropertyContainer {

	private Project project
	private String containerName

	DefaultProjectPropertyContainer(Project project, String containerName) {
		this.project = project
		this.containerName = containerName
	}

	def getProperty(String propertyName) {
		def value = getProjectProperty(propertyName)
		if (value == null) {
			value = getDefaultProperty(propertyName)
		}
		log.debug("getProperty(${propertyName}) : ${value}")
		value
	}

	private def getDefaultProperty(String propertyName) {
		MetaProperty meta = metaClass.getMetaProperty(propertyName)
		meta.getProperty(this)
	}

	private def getProjectProperty(String propertyName) {
		String containerPropertyName = "${containerName}${propertyName.capitalize()}"
		def value = null
		if (project?.hasProperty(containerPropertyName)) {
			value = project.property(containerPropertyName)
		}
		value
	}

}
