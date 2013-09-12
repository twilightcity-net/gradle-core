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

import com.bancvue.zip.ZipArchive
import org.junit.Test

class TestExtPluginIntegrationTest extends AbstractPluginIntegrationTest {

	@Test
	void shouldCompileMainTestSourceAndCreateJarFromSource() {
		projectFS.emptyClassFile("src/mainTest/java/Class.java")
		projectFS.file("build.gradle") << """
apply plugin: 'java'
apply plugin: 'test-ext'

mainTestJar.archiveName='mainTest.jar'
        """

		run("check", "mainTestJar")

		assert projectFS.file("build/classes/mainTest/Class.class").exists()
		ZipArchive mainTestJar = projectFS.archive("build/libs/mainTest.jar")
		assert mainTestJar.exists()
		assert mainTestJar.acquireContentForEntryWithNameLike("Class.class")
	}

}
