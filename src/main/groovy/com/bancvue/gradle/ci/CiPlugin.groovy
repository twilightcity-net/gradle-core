package com.bancvue.gradle.ci

import com.bancvue.gradle.test.JacocoExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import com.bancvue.gradle.maven.publish.MavenPublishExtPlugin

class CiPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'ci'

	private Project project
	
	@Override
	void apply(Project self) {
		this.project = self
		applyRequiredPlugins()
		addCiTasks()
	}

	private void applyRequiredPlugins() {
		project.apply(plugin: MavenPublishExtPlugin.PLUGIN_NAME)
		project.apply(plugin: JacocoExtPlugin.PLUGIN_NAME)
	}

	private void addCiTasks() {
		Task setupCi = project.tasks.create("setupCi")
		Task preCommit = project.tasks.create("preCommit")
		preCommit.configure {
			dependsOn(findTask('clean'), findTask('check'), findTask('coverage'))
			mustRunAfter(setupCi)
		}
		Task ci = project.tasks.create("ci")
		ci.dependsOn(setupCi, preCommit, findTask('publish'))
	}

	private Task findTask(String taskName) {
		project.tasks.findByName(taskName)
	}
}
