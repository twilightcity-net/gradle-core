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
package com.bancvue.gradle.test

import org.gradle.testkit.functional.ExecutionResult
import org.gradle.testkit.functional.GradleRunner
import org.gradle.testkit.functional.GradleRunnerFactory
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractPluginIntegrationSpecification extends Specification {

	@Rule
	public TemporaryFolder projectDir = new TemporaryFolder()
	protected ProjectFileSystem projectFS
	protected GradleRunner runner

	void setup() {
		runner = GradleRunnerFactory.create()
		runner.directory = projectDir.root
		projectFS = new ProjectFileSystem(projectDir.root)
		projectFS.initBuildDir()
	}

	protected ExecutionResult run(String... args) {
		runner.arguments.addAll(args)

		runner.run()
	}

	protected TestFile getBuildFile() {
		projectFS.buildFile
	}

	protected TestFile mkdir(String relativePath) {
		projectFS.mkdir(relativePath)
	}

	protected TestFile file(String relativePath) {
		projectFS.file(relativePath)
	}

	protected TestFile emptyClassFile(String filePath) {
		projectFS.emptyClassFile(filePath)
	}

}
