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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.TempDir

/**
 * Extended by tests which are not testing a plugin itself, but testing classes which are used by plugins and
 * require a project.
 */
abstract class AbstractPluginSupportSpecification extends Specification {

    @TempDir
    protected File projectDir
    protected Project project
    protected ProjectFileSystem projectFS

    void setup() {
        project = createProject()
        projectFS = new ProjectFileSystem(project.rootDir)
    }

    protected Project createProject() {
        ProjectBuilder.builder()
            .withName("plugin-support")
            .withProjectDir(projectDir)
            .build()
    }

}
