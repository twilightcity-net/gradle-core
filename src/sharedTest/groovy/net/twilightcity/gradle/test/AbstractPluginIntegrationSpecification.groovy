/*
 * Copyright 2021 TwilightCity, Inc
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

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.TempDir

abstract class AbstractPluginIntegrationSpecification extends Specification {

    @TempDir
    protected File projectDir
    protected TestGradleBuild testGradleBuild

    void setup() {
        testGradleBuild = new TestGradleBuild(projectDir)
    }

    protected BuildResult run(String... args) {
        testGradleBuild.run(args)
    }

    protected ProjectFileSystem getProjectFS() {
        testGradleBuild.projectFS
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

    protected TestFile emptyClassFile(String filePath, String content = "") {
        projectFS.emptyClassFile(filePath, content)
    }

}
