package com.bancvue.gradle.license

import com.bancvue.gradle.DefaultProjectPropertyContainer
import org.gradle.api.Project


class LicenseExtProperties extends DefaultProjectPropertyContainer {

	private static final String NAME = "license"

	String name
	String headerResourcePath = "/headers/APACHE_HEADER"
	List<String> excludedFileExtensions = ["properties", "json", "yml", "yaml"]

	LicenseExtProperties(Project project) {
		super(project, NAME)
	}

}
