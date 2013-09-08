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
package com.bancvue.gradle.custom

import com.bancvue.gradle.DefaultProjectPropertyContainer
import org.gradle.api.Project


class CustomGradleProperties extends DefaultProjectPropertyContainer {

	private static final String NAME = "customGradle"

	String groupId
	String artifactId
	String version
	String baseVersion
	String scriptResourcePath

	CustomGradleProperties(Project project) {
		super(project, NAME)
	}

}
