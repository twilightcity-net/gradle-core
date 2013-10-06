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

import com.bancvue.gradle.test.AbstractPluginTest
import org.gradle.api.Task
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.maven.MavenDeployer
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.bundling.Jar
import org.junit.Before
import org.junit.Test

class MavenExtPluginTest extends AbstractPluginTest {

	MavenExtPluginTest() {
		super(MavenExtPlugin.PLUGIN_NAME)
	}

	@Before
	void setUp() {
		setArtifactId('artifact')
	}

	@Test
	void apply_ShouldApplyMavenPlugins() {
		applyPlugin()

		assertNamedPluginApplied('maven')
	}

	@Test
	void apply_ShouldAddNexusDependencyRepository() {
		project.ext.repositoryName = 'repo'
		project.ext.repositoryPublicUrl = 'http://public-url'

		applyPlugin()

		MavenArtifactRepository nexusRepo = getMavenRepo('repo')
		assert nexusRepo != null
		assert nexusRepo.url.toString() == 'http://public-url'
	}

	private MavenArtifactRepository getMavenRepo(String name) {
		project.repositories.getByName(name) as MavenArtifactRepository
	}

	@Test
	void apply_ShouldAddMavenLocalDependencyRepository() {
		applyPlugin()

		MavenArtifactRepository mavenLocal = getMavenRepo(ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME)
		assert mavenLocal != null
	}

	@Test
	void apply_ShouldConfigureMavenDeployerReleaseAndSnapshotRepositories() {
		project.version = '1.0'
		project.ext.repositoryReleaseUrl = 'http://release-url'
		project.ext.repositorySnapshotUrl = 'http://snapshot-url'

		applyPlugin()

		MavenDeployer deployer = project.uploadArchives.repositories.mavenDeployer
		assert deployer.repository.url == 'http://release-url'
		assert deployer.snapshotRepository.url == 'http://snapshot-url'
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

	@Test
	void apply_ShouldAliasUploadArchivesTaskToPublishRemote() {
		applyPlugin()
		project.evaluate()

		Task publishRemoteDependency = acquireSingleDependencyForTask('publishRemote')
		assert publishRemoteDependency.name == 'uploadArchives'
	}

	@Test
	void apply_ShouldAliasInstallTaskToPublish() {
		applyPlugin()
		project.evaluate()

		Task publishDependency = acquireSingleDependencyForTask('publish')
		assert publishDependency.name == 'install'
	}

	@Test
	void apply_ShouldSetJarBaseNameToArtifactId_IfArtifactIdSet() {
		setArtifactId('some-artifact')

		applyPlugin()

		assert project.jar.baseName == 'some-artifact'
	}

}
