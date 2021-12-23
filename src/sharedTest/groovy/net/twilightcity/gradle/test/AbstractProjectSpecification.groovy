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
