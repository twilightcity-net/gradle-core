package com.bancvue.gradle.test

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractProjectSpecification extends Specification {

    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder()
    protected Project project
    protected ProjectFileSystem projectFS

    abstract String getProjectName()

    void setup() {
        project = createProject()
        projectFS = new ProjectFileSystem(project.rootDir)
    }

    protected Project createProject() {
        ProjectBuilder.builder()
                .withName("${projectName}-project")
                .withProjectDir(projectDir.root)
                .build()
    }

    protected void setArtifactId(String artifactId) {
        project.ext['artifactId'] = artifactId
    }
}
