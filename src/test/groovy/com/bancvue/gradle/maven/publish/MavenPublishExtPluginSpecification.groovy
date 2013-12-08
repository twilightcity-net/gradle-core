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
package com.bancvue.gradle.maven.publish

import com.bancvue.gradle.test.AbstractPluginSpecification
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

class MavenPublishExtPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		MavenPublishExtPlugin.PLUGIN_NAME
	}

	void setup() {
		setArtifactId('artifact')
	}

	def "apply should apply maven plugin"() {
		when:
		applyPlugin()

		then:
		assertNamedPluginApplied('maven-publish')
	}

	def "apply should add maven artifact repository configured with public url"() {
		given:
		project.ext.repositoryName = 'repo'
		project.ext.repositoryPublicUrl = 'http://public-url'

		when:
		applyPlugin()

		then:
		MavenArtifactRepository repo = getMavenRepo('repo')
		repo != null
		repo.url.toString() == 'http://public-url'
	}

	private MavenArtifactRepository getMavenRepo(String name) {
		project.repositories.getByName(name) as MavenArtifactRepository
	}

	def "apply should add maven local artifact repository"() {
		when:
		applyPlugin()

		then:
		MavenArtifactRepository mavenLocal = getMavenRepo(ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME)
		mavenLocal != null
	}

	private MavenArtifactRepository getMavenPublishingRepo(String name) {
		project.publishing.repositories.getByName(name)
	}

	def "apply should add snapshot repository if version is snapshot"() {
		given:
		project.version = '1.0-SNAPSHOT'
		project.ext.repositoryName = 'repo'
		project.ext.repositorySnapshotUrl = 'http://snapshot-url'

		when:
		applyPlugin()

		then:
		MavenArtifactRepository repo = getMavenPublishingRepo('repo')
		repo.url.toString() == 'http://snapshot-url'
	}

	def "apply should add release repository if version is not snapshot"() {
		given:
		project.version = '1.0'
		project.ext.repositoryName = 'repo'
		project.ext.repositoryReleaseUrl = 'http://release-url'

		when:
		applyPlugin()

		then:
		MavenArtifactRepository repo = getMavenPublishingRepo('repo')
		repo.url.toString() == 'http://release-url'
	}

}
