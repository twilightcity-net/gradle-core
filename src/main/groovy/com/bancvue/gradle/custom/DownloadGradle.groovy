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
package com.bancvue.gradle.custom

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction


class DownloadGradle extends DefaultTask {
	@Input
	String gradleVersion

	@Input
	File destinationDir

	@Input
	String gradleDownloadBase = "http://services.gradle.org/distributions"

	@TaskAction
	doDownloadGradle() {
		destinationFile.bytes = new URL(downloadUrl).bytes
	}

	String getDownloadUrl() {
		"$gradleDownloadBase/$downloadFileName"
	}

	String getDistributionNameBase() {
		"gradle-$gradleVersion"
	}

	String getDownloadFileName() {
		"$distributionNameBase-bin.zip"
	}

	@OutputFile
	File getDestinationFile() {
		new File(destinationDir, downloadFileName)
	}
}