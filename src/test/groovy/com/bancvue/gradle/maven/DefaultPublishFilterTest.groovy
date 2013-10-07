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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

import static org.mockito.Matchers.anyObject
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner)
class DefaultPublishFilterTest {

	@Mock
	private PomFilterContainer container
	@Mock
	private Artifact artifact
	private DefaultPublishFilter defaultFilter
	private List<PomFilter> activePomFilters

	@Before
	void setUp() {
		activePomFilters = []
		defaultFilter = new DefaultPublishFilter(container)
		addActivePomFilter(defaultFilter)
		when(container.getActivePomFilters()).thenReturn(activePomFilters)
	}

	private void addActivePomFilter(PublishFilter filter) {
		activePomFilters << new DefaultPomFilter(null, null, filter)
	}

	@Test
	void accept_ShouldAcceptAllArtifacts_IfNoOtherFilterConfigured() {
		boolean accept = defaultFilter.accept(artifact, null)

		assert accept
	}

	@Test
	void accept_ShouldNotAcceptArtifact_IfAcceptedByOtherFilter() {
		PublishFilter otherFilter = mock(PublishFilter)
		when(otherFilter.accept(anyObject(), anyObject())).thenReturn(true)
		addActivePomFilter(otherFilter)

		boolean accept = defaultFilter.accept(artifact, null)

		assert !accept
	}

}
