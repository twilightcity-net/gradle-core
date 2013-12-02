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
import org.gradle.api.publication.maven.internal.DefaultPomFilter
import org.gradle.api.publication.maven.internal.PomFilter
import spock.lang.Specification

class DefaultPublishFilterSpecification extends Specification {

	private PomFilterContainer container
	private Artifact artifact
	private DefaultPublishFilter defaultFilter
	private List<PomFilter> activePomFilters

	void setup() {
		container = Mock()
		artifact = Mock()
		defaultFilter = new DefaultPublishFilter(container)
		activePomFilters = []
		addActivePomFilter(defaultFilter)
		container.getActivePomFilters() >> activePomFilters
	}

	private void addActivePomFilter(PublishFilter filter) {
		activePomFilters << new DefaultPomFilter(null, null, filter)
	}

	def "accept should accept all artifacts if no other filter configured"() {
		expect:
		defaultFilter.accept(artifact, null)
	}

	def "accept should not accept artifact if accepted by other filter"() {
		given:
		PublishFilter otherFilter = Mock()
		addActivePomFilter(otherFilter)
		otherFilter.accept(_, _) >> true

		expect:
		!defaultFilter.accept(artifact, null)
	}

}
