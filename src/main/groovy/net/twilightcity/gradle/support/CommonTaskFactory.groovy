/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle.support

import net.twilightcity.gradle.categories.ProjectCategory
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class CommonTaskFactory {

	private Project project
	private SourceSet sourceSet
	private TaskAndConfigurationNamer namer

	CommonTaskFactory(Project project, SourceSet sourceSet) {
		this(project, sourceSet, new TaskAndConfigurationNamer(sourceSet.name))
	}

	CommonTaskFactory(Project project, SourceSet sourceSet, TaskAndConfigurationNamer namer) {
		this.project = project
		this.sourceSet = sourceSet
		this.namer = namer
	}

	TaskProvider<Jar> createJarTask() {
		createAndConfigureJarTask(namer.jarTaskName, sourceSet.output)
	}

	TaskProvider<Jar> createSourcesJarTask() {
		createAndConfigureJarTask(namer.sourcesJarTaskName, sourceSet.allSource, "sources")
	}

	TaskProvider<Jar> createAndConfigureJarTask(String jarTaskName, Object sourcePath, String classifierString = null) {
		String postfix = namer.sourceSetNameAppendix
		String jarContent = classifierString ? classifierString : "classes"
		TaskProvider<Jar> jarTaskProvider = project.tasks.register(jarTaskName, Jar)
		jarTaskProvider.configure {
			group = "Build"
			description = "Assembles a jar archive containing the ${sourceSet.name} ${jarContent}."
			if (classifierString) {
				getArchiveClassifier().set(classifierString)
			}
			if (postfix) {
				getArchiveBaseName().set("${getArchiveBaseName().get()}-${postfix}")
			}
			from sourcePath
		}
		return jarTaskProvider
	}

	TaskProvider<Jar> createJavadocJarTask() {
		TaskProvider<Javadoc> javadocTask = project.tasks.named(namer.javadocTaskName)
		if (javadocTask == null) {
			javadocTask = createJavadocTask()
		}
		TaskProvider<Jar> javadocJarTaskProvider = createAndConfigureJarTask(
				namer.javadocJarTaskName, javadocTask.get().destinationDir, "javadoc"
		)
		javadocJarTaskProvider.configure {
			dependsOn(javadocTask)
		}
		javadocJarTaskProvider
	}

	TaskProvider<Javadoc> createJavadocTask() {
		File javadocsDir = getJavadocsDir()
		TaskProvider<Javadoc> javadocTaskProvider = project.tasks.register(namer.javadocTaskName, Javadoc)
		javadocTaskProvider.configure {
			source = sourceSet.allJava
			classpath = sourceSet.output + sourceSet.compileClasspath
			group = JavaBasePlugin.DOCUMENTATION_GROUP
			description = "Generates Javadoc API documentation for the ${sourceSet.name} source code."
			destinationDir = javadocsDir
		}
		javadocTaskProvider
	}

	private File getJavadocsDir() {
		use(ProjectCategory) {
			new File(project.getJavaConvention().docsDir, "${sourceSet.name}Docs")
		}
	}

}
