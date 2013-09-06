package com.bancvue.gradle.test

import org.apache.commons.io.FilenameUtils


class ProjectFileSystem extends TestFile {

	ProjectFileSystem(File baseDir) {
		super(baseDir)
	}

	void initBuildDir() {
		mkdir('build')
	}

	TestFile buildFile() {
		file("build.gradle")
	}

	TestFile emptyClassFile(String filePath) {
		TestFile classFile = file(filePath)
		String className = FilenameUtils.getBaseName(classFile.name)
		classFile << "class ${className} {}"
		classFile
	}
}

