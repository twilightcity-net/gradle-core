/*
 * Copyright 2014 BancVue, LTD
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
