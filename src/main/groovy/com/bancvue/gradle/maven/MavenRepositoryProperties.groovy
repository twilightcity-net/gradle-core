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

	public static final String NAME = "repository";

	private static final String getDefaultUrlPropertyValue(String propertyName) {
		"changeme:set-project-property-repository${propertyName.capitalize()}"
	}

	String name = "repo"
	String publicUrl = getDefaultUrlPropertyValue("publicUrl")
	String snapshotUrl = getDefaultUrlPropertyValue("snapshotUrl")
	String releaseUrl = getDefaultUrlPropertyValue("releaseUrl")
	String username
	String password

	MavenRepositoryProperties(Project project) {
		super(project, NAME)
	}

	boolean hasCredentialsDefined() {
		// see http://groovy.329449.n5.nabble.com/How-invoke-getProperty-interceptor-from-this-td5712559.html
		// for why the getProperty usage is necessary
		getProperty("username") && getProperty("password")
	}

}
