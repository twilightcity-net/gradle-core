/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle.maven.publish

import com.bancvue.gradle.categories.ProjectCategory
import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.ConfigureUtil

@Slf4j
class ExtendedPublication {

	private String id
	private ExtendedPublicationNameResolver resolver

	String artifactId
	Project project
	AbstractArchiveTask archiveTask
	AbstractArchiveTask sourcesArchiveTask
	SourceSet sourceSet
	Configuration runtimeConfiguration
	boolean enabled
	boolean publishSources
	Closure config
	Closure pom

	ExtendedPublication(String id, Project project) {
		this.id = id
		this.project = project
		this.enabled = true
		this.publishSources = true
		this.resolver = new ExtendedPublicationNameResolver(id)
	}

	String getName() {
		artifactId.replaceAll(/[-](\S)/) { it[1].toUpperCase() }
	}

	void configure(Closure configure) {
		ConfigureUtil.configure(configure, this)
	}

	/**
	 * Jar tasks are resolved post evaluation since the jar task is dynamically created if it can't be found.
	 * If this was done pre-evaluation, it could conflict with an as-yet-to-be-defined jar task.
	 */
	void deriveUnsetVariables() {
		setArtifactIdIfNotSet()
		setRuntimeConfigurationIfNotSet()
		// NOTE: order is important!  sourceSet must be defined prior to the jar tasks since they may require
		// the sourceSet if the tasks need to be dynamically constructed
		setSourceSetIfNotSet()
		setJarTaskIfNotSet()
		if (publishSources) {
			setSourceJarTaskIfNotSet()
		}
	}

	boolean isArchiveAttachedToRuntimeConfiguration() {
		runtimeConfiguration.artifacts.find { PublishArtifact artifact ->
			archiveTask && (artifact.file == archiveTask.archivePath)
		}
	}

	private void setArtifactIdIfNotSet() {
		if (artifactId == null) {
			artifactId = resolver.getArtifactId(project)
		}
	}

	private void setJarTaskIfNotSet() {
		if (archiveTask == null) {
			archiveTask = findOrCreateJarTask()
		}
	}

	private void setSourceJarTaskIfNotSet() {
		if (sourcesArchiveTask == null) {
			sourcesArchiveTask = findOrCreateSourcesJarTask()
		}
	}

	private void setSourceSetIfNotSet() {
		if (sourceSet == null) {
			// findByName is used b/c SourceSet is optional; if not set and no archive tasks are available,
			// then no archives will be attached to the maven publication
			sourceSet = project.sourceSets.findByName(resolver.sourceSetName)
		}
	}

	private void setRuntimeConfigurationIfNotSet() {
		if (runtimeConfiguration == null) {
			runtimeConfiguration = project.configurations.getByName(resolver.runtimeConfigurationName)
		}
	}

	private Jar findOrCreateJarTask() {
		resolveJarTask(false)
	}

	private Jar findOrCreateSourcesJarTask() {
		resolveJarTask(true)
	}

	private Jar resolveJarTask(boolean isSourceJar) {
		String jarTaskName = isSourceJar ? resolver.sourcesJarTaskName : resolver.jarTaskName
		Jar jarTask = project.tasks.findByName(jarTaskName)

		if (jarTask == null) {
			jarTask = createJarTaskIfSourceSetAvailable(jarTaskName, isSourceJar)
		}
		jarTask
	}

	private Jar createJarTaskIfSourceSetAvailable(String jarTaskName, boolean isSourceJar) {
		Jar jarTask = null
		if (sourceSet != null) {
			jarTask = createJarTask(jarTaskName, isSourceJar)
		} else {
			log.warn("Failed to resolve archive task=${jarTaskName} and unable to create task due to undefined SourceSet")
		}
		jarTask
	}

	private Jar createJarTask(String jarTaskName, boolean isSourceJar) {
		String postfix = resolver.publicationIdAppendix
		String classifierString = isSourceJar ? "sources" : null

		Jar jarTask = ProjectCategory.createJarTask(project, jarTaskName, sourceSet.name, classifierString)
		jarTask.configure {
			if (postfix) {
				baseName = "${baseName}-${postfix}"
			}
			from isSourceJar ? getSourceSet().allSource : getSourceSet().output
		}
		project.getTasks().getByName(BasePlugin.ASSEMBLE_TASK_NAME).dependsOn(jarTask)
		jarTask
	}

	/*
	 * TODO: figure out why this is necessary...
	 * The idea here is to allow configuration in the dsl on par with how regular maven publications are configured, e.g.
	 *
	 * publication("main") {
	 *   enabled false
	 * }
	 *
	 * rather than
	 *
	 * publication("main") {
	 * 	 enabled = false
	 * }
	 *
	 * ConfigureUtil.configure explodes unless the enabled(boolean) method is defined, a regular property won't do.
	 * However, some of the gradle core classes (e.g. DefaultMavenPublication) don't seem to need them, need to
	 * investigate why.
	 */
	def methodMissing(String methodName, args) {
		Object[] argArray = args as Object[]
		MetaProperty property = hasProperty(methodName)

		if (property && (argArray.length == 1)) {
			// yes, this is wonky but it works for primitives whereas property.type.isInstance(argArray[0]) does not...
			try {
				property.setProperty(this, argArray[0])
				return
			} catch (Exception ex) {
			}
		}
		throw new MissingMethodException(methodName, this.class, args)
	}

}
