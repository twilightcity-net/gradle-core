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


class TestFile extends File {

	TestFile(File file) {
		super(file.toURI())
	}


	TestFile mkdir(String relativePath){
		TestFile dir = file(relativePath)
		dir.mkdirs()
		dir
	}

	ZipArchive archive(String relativePath) {
		TestFile file = file(relativePath)
		new ZipArchive(file)
	}

	TestFile file(String relativePath) {
		File file = new File(this, relativePath)
		new TestFile(file)
	}

	TestFile file(String relativePath, String fileName) {
		File parentDir = file(relativePath)
		new TestFile(new File(parentDir, fileName))
	}

	TestFile leftShift(Object content) {
		parentFile.mkdirs()
		super.leftShift(content)
		this
	}

	URL toURL() {
		toURI().toURL()
	}

}
