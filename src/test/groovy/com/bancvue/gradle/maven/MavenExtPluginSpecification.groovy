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

import com.bancvue.gradle.test.AbstractPluginSpecification
import org.gradle.api.Task
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.maven.MavenDeployer
import org.gradle.api.artifacts.repositories.MavenArtifactRepository

class MavenExtPluginSpecification extends AbstractPluginSpecification {

	String getPluginName() {
		MavenExtPlugin.PLUGIN_NAME
	}

	void setup() {
		setArtifactId('artifact')
	}

	def "apply should apply maven plugins"() {
		when:
		applyPlugin()

		then:
		assertNamedPluginApplied('maven')
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
		getMavenRepo(ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME) != null
	}

	def "apply should configure maven deployer release and snapshot repositories"() {
		given:
		project.version = '1.0'
		project.ext.repositoryReleaseUrl = 'http://release-url'
		project.ext.repositorySnapshotUrl = 'http://snapshot-url'

		when:
		applyPlugin()

		then:
		MavenDeployer deployer = project.uploadArchives.repositories.mavenDeployer
		deployer.repository.url == 'http://release-url'
		deployer.snapshotRepository.url == 'http://snapshot-url'
	}

	private Task acquireSingleDependencyForTask(String taskName) {
		List dependencies = getDependenciesForTask(taskName)
		assert dependencies.size() == 1
		dependencies[0]
	}

	private List getDependenciesForTask(String taskName) {
		Task task = project.tasks.findByName(taskName)
		task.taskDependencies.getDependencies(task).toList()
	}

	def "apply should alias uploadArchives task to publish"() {
		when:
		applyPlugin()
		project.evaluate()

		then:
		Task publishRemoteDependency = acquireSingleDependencyForTask('publish')
		publishRemoteDependency.name == 'uploadArchives'
	}

	def "apply should alias install task to publishLocal"() {
		when:
		applyPlugin()
		project.evaluate()

		then:
		Task publishDependency = acquireSingleDependencyForTask('publishLocal')
		publishDependency.name == 'install'
	}

}
