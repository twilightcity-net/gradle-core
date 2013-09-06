package com.bancvue.gradle.test


class TestFile extends File {

	TestFile(File file) {
		super(file.toURI())
	}


	TestFile mkdir(String relativePath){
		TestFile dir = file(relativePath)
		dir.mkdirs()
		dir
	}

	TestFile file(String relativePath) {
		File file = new File(this, relativePath)
		new TestFile(file)
	}

	TestFile file(String relativePath, String fileName) {
		File parentDir = file(relativePath)
		new TestFile(new File(parentDir, fileName))
	}

	public TestFile leftShift(Object content) {
		parentFile.mkdirs()
		super.leftShift(content)
		this
	}

}
