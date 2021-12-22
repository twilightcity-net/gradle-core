/*
 * Copyright 2018 DreamScale, Inc
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
package org.dreamscale.gradle.test

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class TestGradleBuild {

    boolean runWithStacktrace = true
    ProjectFileSystem projectFS

    TestGradleBuild(File baseDir) {
        projectFS = new ProjectFileSystem(baseDir)
        projectFS.initBuildDir()
        initBuildscriptPluginPathString()
    }

    /**
     * This is a hack to work around the fact the tooling API does not yet provide a good mechanism to inject the code
     * under test
     * @see https://docs.gradle.org/current/userguide/test_kit.html
     */
    void initBuildscriptPluginPathString() {
        String pluginClasspath = System.getProperty("java.class.path").split(File.pathSeparator)
                .collect { "'$it'" }
                .join(", ")

        projectFS.buildFile << """
buildscript {
    dependencies {
        classpath files($pluginClasspath)
    }
}
"""
    }

    BuildResult run(String... args) {
        List<String> argList = args as List
        if (runWithStacktrace) {
            argList.add("--stacktrace")
        }

        GradleRunner.create()
                .withProjectDir(projectFS)
                .withArguments(argList)
                .build()
    }

    TestFile getBuildFile() {
        projectFS.buildFile
    }

}
