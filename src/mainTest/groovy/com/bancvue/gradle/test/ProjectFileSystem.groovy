package com.bancvue.gradle.test


class ProjectFileSystem extends TestFile {

	ProjectFileSystem(File baseDir) {
		super(baseDir)
	}

	void initBuildDir() {
		mkdir('build')
	}

	File buildFile() {
		file("build.gradle")
	}

}

