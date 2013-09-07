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
package com.bancvue.zip

import java.util.zip.ZipEntry
import java.util.zip.ZipFile


class ZipArchive {

	private File file
	private ZipFile zip

	ZipArchive(File file) {
		this.file = file
		this.zip = new ZipFile(file)
	}

	String getContentForEntryWithNameLike(String name) {
		ZipEntry entry = zip.entries().find { ZipEntry entry ->
			entry.name =~ /${name}/
		}
		if (entry == null) {
			throw new RuntimeException("No entry with name like '${name}' in file=${file.absolutePath}")
		}
		zip.getInputStream(entry).text
	}
}
