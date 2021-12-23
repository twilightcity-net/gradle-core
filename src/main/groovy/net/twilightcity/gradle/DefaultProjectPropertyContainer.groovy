/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle

import groovy.util.logging.Slf4j
import org.gradle.api.Project

/**
 * NOTE: due to how groovy routes property access, sub-classes should encapsulate their properties in an inner class
 * and declare an instance of this properties class annotated with @Delegate.
 * Otherwise they would need to use getProperty(<property name>) to access their own properties, which is non-obvious
 * and error-prone.
 * see http://groovy.329449.n5.nabble.com/How-invoke-getProperty-interceptor-from-this-td5712559.html
 */
@Slf4j
class DefaultProjectPropertyContainer {

	private String containerName
	protected Project project
	protected ResourceResolver resourceResolver

	DefaultProjectPropertyContainer(Project project, String containerName) {
		this.project = project
		this.containerName = containerName
		this.resourceResolver = ResourceResolver.create(project)
	}

	def getProperty(String propertyName) {
		def value = getProjectProperty(propertyName)
		if (value == null) {
			value = getDefaultProperty(propertyName)
		}
		log.debug("[${containerName}] getProperty(${propertyName}) : ${value}")
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
