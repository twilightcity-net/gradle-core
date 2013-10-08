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
package com.bancvue.gradle.maven

import com.bancvue.gradle.categories.ProjectCategory
import org.gradle.api.Project

class MavenPublishExtExtension {

	static final String NAME = "publishingext"

	String primaryArtifactName

	private Project project
	private ExtendedPublicationContainer closureMap

	MavenPublishExtExtension(Project project) {
		this.project = project
		this.closureMap = new ExtendedPublicationContainer()
		primaryArtifactName = getDefaultPrimaryArtifactName()
	}

	private String getDefaultPrimaryArtifactName() {
		String projectName = ProjectCategory.getArtifactId(project)
		if (projectName == null) {
			projectName = project.name
		}
		projectName.replaceAll(/[-](\S)/) { it[1].toUpperCase() }
	}

	void publications(Closure configure) {
		closureMap.capture(configure)
	}

	List<ExtendedPublication> getExtendedPublications() {
		closureMap.getExtendedPublications()
	}

	ExtendedPublication getExtendedPublication(String publicationName) {
		closureMap.getExtendedPublication(publicationName)
	}

}
