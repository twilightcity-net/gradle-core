package com.bancvue.gradle.license

import nl.javadude.gradle.plugins.license.License
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet


class LicenseExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'license-ext'
	private static final String GROUP_NAME = 'License'

	private Project project
	private LicenseExtProperties licenseProperties

	@Override
	void apply(Project project) {
		this.project = project
		licenseProperties = new LicenseExtProperties(project)
		applyLicensePlugin()
		configureApache2LicenseHeader()
		setGroupOnAllFormatLicenseTasks()
		addFormatAllLicenseTask()
		addCheckAllLicenseTask()
	}

	private void applyLicensePlugin() {
		project.apply(plugin: 'license')
	}

	private void configureApache2LicenseHeader() {
		addTaskToWriteDefaultHeaderPriorToLicenseExecution()
		project.license {
			header = getDefaultHeaderFile()
			ext.year = Calendar.getInstance().get(Calendar.YEAR)
			if (licenseProperties.name) {
				ext.name = licenseProperties.name
			}
		}
	}

	private void addTaskToWriteDefaultHeaderPriorToLicenseExecution() {
		Task writeHeaderResourceToFile = project.tasks.create('writeDefaultHeaderFile')
		writeHeaderResourceToFile.doLast {
			writeDefaultHeaderFileIfHeaderNotOverridden()
		}

		project.tasks.withType(License).findAll { License task ->
			task.dependsOn(writeHeaderResourceToFile)
		}
	}

	private void writeDefaultHeaderFileIfHeaderNotOverridden() {
		if (isDefaultHeaderSetOnAnyLicenseTask()) {
			File defaultHeaderFile = getDefaultHeaderFile()
			defaultHeaderFile.parentFile.mkdirs()
			defaultHeaderFile << acquireHeaderResourceContent()
		}
	}

	private boolean isDefaultHeaderSetOnAnyLicenseTask() {
		File defaultHeaderFile = getDefaultHeaderFile()

		project.tasks.withType(License).find { License task ->
			task.header == defaultHeaderFile
		}
	}

	private String acquireHeaderResourceContent() {
		String resourceName = licenseProperties.headerResourcePath
		def resource = getHeaderFromSourceSetOrPath(resourceName)
		if (resource == null) {
			throw new RuntimeException("Failed to resolve resource with name=${resourceName}")
		}
		resource.text
	}

	private def getHeaderFromSourceSetOrPath(String resourceName) {
		def resource = getHeaderResourceFromProjectSourceSets(resourceName)
		if (resource == null) {
			resource = getClass().getResource(resourceName)
		}
		resource
	}

	private File getHeaderResourceFromProjectSourceSets(String resourceName) {
		File headerResourceFile = null
		def srcDirs = project.sourceSets.collect { SourceSet sourceSet ->
			sourceSet.resources.srcDirs
		}

		srcDirs*.each { File srcDir ->
			File file = new File(srcDir, resourceName)
			if (!headerResourceFile && file.exists()) {
				headerResourceFile = file
			}
		}
		headerResourceFile
	}

	private String acquireResourceAsText(String resourceName) {
		URL resource = getClass().getResource(resourceName)
		if (resource == null) {
			throw new RuntimeException("Failed to resolve resource with name=${resourceName}")
		}
		resource.text
	}

	private File getDefaultHeaderFile() {
		new File(project.buildDir, licenseProperties.headerResourcePath)
	}

	private void setGroupOnAllFormatLicenseTasks() {
		getFormatLicensesTasks().each { License task ->
			task.group = GROUP_NAME
		}
	}

	private void addFormatAllLicenseTask() {
		Task licenseFormat = project.tasks.create('licenseFormat')
		licenseFormat.group = GROUP_NAME
		licenseFormat.description = 'Apply license on files from all available source sets'

		getFormatLicensesTasks().each { License task ->
			licenseFormat.dependsOn(task)
		}
	}

	private void addCheckAllLicenseTask() {
		Task licenseCheck = project.tasks.create('licenseCheck')
		licenseCheck.group = GROUP_NAME
		licenseCheck.description = 'Check license on files from all available source sets'

		getCheckLicensesTasks().each { License task ->
			licenseCheck.dependsOn(task)
		}
	}

	private Set<License> getCheckLicensesTasks() {
		project.tasks.withType(License).findAll { License task ->
			task.check
		}
	}

	private Set<License> getFormatLicensesTasks() {
		project.tasks.withType(License).findAll { License task ->
			task.check == false
		}
	}

}
