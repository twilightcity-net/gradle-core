/*
 * Copyright 2014 BancVue, LTD
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
package com.bancvue.gradle.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractProjectSpecification extends Specification {

    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder()
	@Shared
    protected Project project
    protected ProjectFileSystem projectFS

    abstract String getProjectName()

    void setup() {
        project = createProject()
        projectFS = new ProjectFileSystem(project.rootDir)
    }

	protected void evaluateProject() {
		project.evaluate()
	}

    protected Project createProject() {
        ProjectBuilder.builder()
                .withName("${projectName}-project")
                .withProjectDir(projectDir.root)
                .build()
    }

	protected Project createSubProject(String subProjectName) {
		File subProjectDir = projectFS.file(subProjectName)

		ProjectBuilder.builder()
				.withName(subProjectName)
				.withProjectDir(subProjectDir)
				.withParent(project)
				.build()
	}

	protected void setArtifactId(String artifactId) {
        project.ext['artifactId'] = artifactId
    }
}
