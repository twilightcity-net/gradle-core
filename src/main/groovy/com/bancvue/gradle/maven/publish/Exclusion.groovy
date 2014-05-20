package com.bancvue.gradle.maven.publish

import org.gradle.api.artifacts.ExcludeRule

class Exclusion {
	private String groupId
	private String artifactId

	public Exclusion(ExcludeRule excludeRule) {
		groupId = excludeRule.group
		artifactId = excludeRule.module
	}

	String getGroupId() {
		groupId ?: "*"
	}

	String getArtifactId() {
		artifactId ?: "*"
	}
}