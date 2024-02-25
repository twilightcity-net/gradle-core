/*
 * Copyright 2022 TwilightCity, Inc
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
package net.twilightcity.gradle.test

import com.google.common.io.Files
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

abstract class AbstractProjectSpecification extends Specification {

    private File projectDir = Files.createTempDir()
    private Project aProject = createProject()
    private ProjectFileSystem aProjectFS = new ProjectFileSystem(aProject.rootDir)

    void cleanup() {
        projectDir.deleteDir()
    }

    protected String getProjectName() {
        "root"
    }

    protected Project getProject() {
        aProject
    }

    protected void setProject(Project project) {
        aProject = project
    }

    protected ProjectFileSystem getProjectFS() {
        aProjectFS
    }

    protected void evaluateProject() {
        aProject.evaluate()
    }

    protected Project createProject() {
        ProjectBuilder.builder()
                .withName("${projectName}-project")
                .withProjectDir(projectDir)
                .build()
    }

    protected Project createSubProject(String subProjectName) {
        File subProjectDir = aProjectFS.file(subProjectName)

        ProjectBuilder.builder()
                .withName(subProjectName)
                .withProjectDir(subProjectDir)
                .withParent(aProject)
                .build()
    }

    protected void setArtifactId(String artifactId) {
        aProject.ext['artifactId'] = artifactId
    }
}
