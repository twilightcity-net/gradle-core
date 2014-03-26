package com.bancvue.gradle.ci

import com.bancvue.gradle.maven.publish.MavenPublishExtPlugin
import com.bancvue.gradle.test.AbstractPluginSpecification
import com.bancvue.gradle.test.JacocoExtPlugin

class CiPluginSpecification extends AbstractPluginSpecification {

	@Override
	String getPluginName() {
		CiPlugin.PLUGIN_NAME
	}

	def "apply should add ci task"() {
		given:
		assert project.tasks.findByName('ci') == null

		when:
		applyPlugin()
		
		then:
		assert project.tasks.findByName('ci') != null
	}
	
	def "apply should add setupCi task"() {
		given:
		assert project.tasks.findByName('setupCi') == null

		when:
		applyPlugin()
		
		then:
		assert project.tasks.findByName('setupCi') != null
	}
}
