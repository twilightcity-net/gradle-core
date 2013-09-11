# GradlePluginsCore

A collection of gradle plugins meant to codify and streamline project development within an organization.  



## DefaultProjectPropertyContainer

It can be useful to configure a plugin before the plugin is actually applied.  For example, organization-wide defaults
can be configured in a custom gradle build and distributed to all projects; a given project may use all or just some of
these settings, depending on which plugins that project actually applies.  

	class ProjectDefaultsProperties extends DefaultProjectPropertyContainer {

		private static final String NAME = 'default'

		String javaVersion = '1.7'

		String compilerEncoding = 'UTF-8'

		...
	}


## ProjectDefaultsPlugin (bancvue-defaults)

This plugin sets system-level gradle project defaults.  Specifically, java version and memory settings for spawned
processes.  By default, gradle forks processes for both compilation and testing and the memory settings of these spawned
processes default to the settings of the underlying system, which may be very different between dev machine and CI server.

The plugin defaults (jdk, compiler encoding, [min/max heap size, max perm gen size] for compile and test) can be found
in <TODO: add link> com.bancvue.gradle.ProjectDefaultsProperties.  If the plugin is applied and nothing more is done, these
project defaults will be applied.  Alternative defaults can be defined through a customized gradle build <TODO: add link>.
Finally, any setting can be overridden on a per-project basis.

The project jar's manifest is updated with the following attribute/values...

 * Built-Date - the current date
 * Build-Jdk - the value of the 'java.version' system property





## MavenPublishExtPlugin (maven-publish-ext)

The [maven-publish](http://www.gradle.org/docs/current/userguide/publishing_maven.html)
and [maven-publish-auth](https://github.com/sebersole/gradle-maven-publish-auth)
plugins are applied, adding support for dependency resolution and artifact publishing.

The 'repository' project defaults (found here <TODO: add link> com.bancvue.gradle.maven.MavenRepositoryProperties)
are allow for local maven publication with no configuration.  Organization defaults can be defined to retrieve
dependencies from and publish to a central repository.


#### Added tasks

* sourceJar, generate a jar with project source files
* javadocJar, generate a jar with project javadoc
* maven publication, publish the project artifact.  source jar is published with the 'sources' classifier.  source test
jar is published with the 'test' classifier.


## TestExtPlugin (test-ext)

        addMainTestConfigurationIfMainTestDirDefined()

#### Added tasks

* styledTestOutput, adds incremental (and colored) output when running tests, especially useful with longer-running tests to visually track progress

#### Logging updates to Test tasks

Full stack traces are enabled rather than just the first line of the exception.  For example, if a Groovy power
assertion fails, the complete failure will be displayed on the console rather than forcing you to open up
the test results.  In addition, groovy stack trace filters are enabled so that the groovy internals are excluded
from all exception stack traces.

Skipped events are output to the console in addition to test faiures.


## NamedTestConfigurationPlugin (component-test, integration-test)

This plugin is not directly applied but is meant for extension by other plugins.

The configuration classpath order is main (dependencies and compilation output), test (only dependencies, no
compilation dependecies), <named test configuration> dependencies
TODO: shouldn't <named test configuration> dependencies come before test dependencies?

## ComponentTestPlugin (component-test)

Adds a test configuration named 'component-test'


## IntegrationTestPlugin (integration-test)

Adds a test configuration named 'integration-test'.


## IdeExtPlugin (ide-ext)

The [idea](http://www.gradle.org/docs/current/userguide/idea_plugin.html)
and [eclipse](http://www.gradle.org/docs/current/userguide/idea_plugin.html)
plugins are applied, adding support for generating IDE project files for both IDEA and Eclipse.

This plugin adds a number of 'refresh' tasks which combine the clean and create tasks of the respective IDE plugin.
In addition, any named test configurations are automatically detected and the source dirs added to the project
source sets and the dependencies added to the project classpath.

#### Added tasks

* refreshIdea, wipe out and regenerate the IDEA project, workspace and module files
* refreshIdeaModule, wipe out and regenerate the IDEA module file
* refreshEclipse, wipe out and regenerate the Eclipse classpath and project files


## ProjectSupportPlugin (project-support)

Build support plugins.

* printClasspath, print the classpath of all (or specific, see 'gradle tasks') source sets of the current project.
  Especially useful for debugging buidl tasks and plugins.
* clearGroupCache, clears the local maven repository and local gradle cache of all artifacts with the project's group id.
  Can be incorporated into dev process tasks such as 'pre-commit'.

## CustomGradlePlugin (custom-gradle)

A plugin for generating a custom gradle build which can then be used to codify certain
attributes (e.g. artifact repository urls) or behavior (e.g. default plugins) for an
organization.  The idea is to provide a gradle wrapper customized to an organization
which can used for all internal projects.


## BancvueProjectPlugin (bancvue)

The 'root' of the Bancvue plugin heirarchy, this plugin defines behavior all Bancvue projects will likely need.

The [java](http://www.gradle.org/docs/current/userguide/java_plugin.html)
and [groovy](http://www.gradle.org/docs/current/userguide/groovy_plugin.html) plugins are applied first.

All other behavior is split into individual plugins covered below.  Each of these plugins (except integration test)
is applied as part of the BancvuePlugin.

# Gradle Test Kit

The content of src/mainTest/java/ is a copy of Luke Daley's gradle test kit prototype (https://github.com/alkemist/gradle-test-kit).
There is an open ticket (https://github.com/alkemist/gradle-test-kit/issues/3) for this to be published to central.
If that happens (or better yet, something is released as part of gradle to aid in integration testing), this code
can be removed.

As of this writing, this code is released under the Apache License 2.0, http://www.apache.org/licenses/LICENSE-2.0
