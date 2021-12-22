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
package net.twilightcity.gradle.categories

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention


class ProjectCategory {

	static class ArtifactIdNotDefinedException extends GradleException {
		public ArtifactIdNotDefinedException() {
			super('Required property "artifactId" must be specified')
		}
	}

	static String getArtifactId(Project self) {
		String artifactId = getArtifactIdOrNull(self)
		if (artifactId == null) {
			throw new ArtifactIdNotDefinedException()
		}
		artifactId
	}

	static String getArtifactIdOrNull(Project self) {
		self.hasProperty('artifactId') ? self.ext.artifactId : null
	}

	static JavaPluginConvention getJavaConvention(Project self) {
		self.getConvention().getPlugins().get("java") as JavaPluginConvention
	}

}
