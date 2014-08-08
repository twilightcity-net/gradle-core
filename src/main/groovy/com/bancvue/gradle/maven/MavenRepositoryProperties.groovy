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
package com.bancvue.gradle.maven

import com.bancvue.gradle.DefaultProjectPropertyContainer
import org.gradle.api.Project

class MavenRepositoryProperties extends DefaultProjectPropertyContainer {

	public static final String NAME = "repository"

	private static final String getDefaultUrlPropertyValue(String propertyName) {
		"http://set_project_property/repository${propertyName.capitalize()}"
	}

	private static class Props {
		String name = "repo"
		/**
		 * Dependency repository public url.
		 */
		String publicUrl = getDefaultUrlPropertyValue("publicUrl")
		/**
		 * Publication repository snapshot url.
		 */
		String snapshotUrl = getDefaultUrlPropertyValue("snapshotUrl")
		/**
		 * Publication repository release url.
		 */
		String releaseUrl = getDefaultUrlPropertyValue("releaseUrl")
		/**
		 * Publication repository username.  Only needed if publishing to remote repository; this will generally only
		 * happen on a CI server and will be passed in by the CI job.
		 */
		String username
		/**
		 * Publication repository password.  Only needed if publishing to remote repository; this will generally only
		 * happen on a CI server and will be passed in by the CI job.
		 */
		String password
	}

	@Delegate
	private Props props = new Props()

	MavenRepositoryProperties(Project project) {
		super(project, NAME)
	}

	boolean hasCredentialsDefined() {
		username && password
	}

}
