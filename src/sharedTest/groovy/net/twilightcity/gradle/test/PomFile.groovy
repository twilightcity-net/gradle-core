package net.twilightcity.gradle.test

class PomFile extends File {

    private def pom

    PomFile(File file) {
        super(file.toURI())
        pom = new XmlParser().parse(this)
    }

    int getDependencyCount() {
        pom.dependencies.dependency.size()
    }

    Node findDependencyNode(String artifactId) {
        pom.dependencies.dependency.find { it.artifactId.text() == artifactId }
    }

    void assertDependency(String artifactId) {
        Node dependency = findDependencyNode(artifactId)
        assert dependency: "Dependency for artifactId=${artifactId} not found"
    }

    void assertNoDependency(String artifactId) {
        Node dependency = findDependencyNode(artifactId)
        assert !dependency: "Dependency unexpectedly found for artifactId=${artifactId}"
    }

    Node findExclusionNode(String dependencyArtifactId, String exclusionGroupId, String exclusionArtifactId) {
        Node dependency = pom.dependencies.dependency.find { it.artifactId.text() == dependencyArtifactId }
        dependency.exclusions.exclusion.find {
            boolean groupIdMatch = (exclusionGroupId ? (it.groupId.text() == exclusionGroupId) : true)
            boolean artifactIdMatch = (exclusionArtifactId ? (it.artifactId.text() == exclusionArtifactId) : true)
            groupIdMatch && artifactIdMatch
        }
    }

    void assertExclusion(String dependencyArtifactId, String exclusionGroupId, String exclusionArtifactId) {
        Node exclusion = findExclusionNode(dependencyArtifactId, exclusionGroupId, exclusionArtifactId)
        assert exclusion: "Exclusion for dependency=${dependencyArtifactId} not found, exclusion=${exclusionGroupId}:${exclusionArtifactId}"
    }

}
