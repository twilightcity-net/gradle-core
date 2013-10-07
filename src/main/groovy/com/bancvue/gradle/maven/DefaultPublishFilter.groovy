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

import org.apache.ivy.core.module.descriptor.Artifact
import org.gradle.api.artifacts.maven.PomFilterContainer
import org.gradle.api.artifacts.maven.PublishFilter
import org.gradle.api.publication.maven.internal.PomFilter


class DefaultPublishFilter implements PublishFilter {

	private PomFilterContainer container

	DefaultPublishFilter(PomFilterContainer container) {
		this.container = container
	}

	@Override
	boolean accept(Artifact artifact, File file) {
		boolean acceptedByExistingFilter = false
		container.activePomFilters.each { PomFilter pomFilter ->
			if (this != pomFilter.filter) {
				acceptedByExistingFilter = acceptedByExistingFilter || pomFilter.filter.accept(artifact, file)
			}
		}
		!acceptedByExistingFilter
	}

}