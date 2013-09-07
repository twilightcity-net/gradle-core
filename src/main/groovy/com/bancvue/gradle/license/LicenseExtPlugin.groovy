package com.bancvue.gradle.license

import nl.javadude.gradle.plugins.license.License
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class LicenseExtPlugin implements Plugin<Project> {

	static final String PLUGIN_NAME = 'license-ext'
	private static final String GROUP_NAME = 'License'

	private Project project
	private LicenseExtProperties licenseProperties
	private HeaderContentResolver headerContentResolver

	@Override
	void apply(Project project) {
		init(project)
		applyLicensePlugin()
		configureApache2LicenseHeader()
		setGroupOnAllFormatLicenseTasks()
		excludedConfiguredFileExtensions()
		addFormatAllLicenseTask()
		addCheckAllLicenseTask()
	}

	private void init(Project project) {
		this.project = project
		licenseProperties = new LicenseExtProperties(project)
		headerContentResolver = new HeaderContentResolver.Impl(project)
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
			writeDefaultHeaderFile()
		}
	}

	private void writeDefaultHeaderFile() {
		File defaultHeaderFile = getDefaultHeaderFile()
		defaultHeaderFile.parentFile.mkdirs()
		defaultHeaderFile.write(acquireHeaderResourceContent())
	}

	private boolean isDefaultHeaderSetOnAnyLicenseTask() {
		File defaultHeaderFile = getDefaultHeaderFile()

		project.tasks.withType(License).find { License task ->
			task.header == defaultHeaderFile
		}
	}

	private File getDefaultHeaderFile() {
		new File(project.buildDir, licenseProperties.headerResourcePath)
	}

	private String acquireHeaderResourceContent() {
		headerContentResolver.acquireHeaderResourceContent(licenseProperties.headerResourcePath)
	}

	private void setGroupOnAllFormatLicenseTasks() {
		getFormatLicensesTasks().each { License task ->
			task.group = GROUP_NAME
		}
	}

	private void excludedConfiguredFileExtensions() {
		List<String> expressions = getExcludedFileExpressions()
		if (expressions) {
			project.tasks.withType(License).each { License licenseTask ->
				licenseTask.exclude expressions
			}
		}
	}

	private List<String> getExcludedFileExpressions() {
		List<String> expressions = []
		if (licenseProperties) {
			expressions = licenseProperties.excludedFileExtensions.collect { String extension ->
				"**/*.${extension}"
			}
		}
		expressions
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
