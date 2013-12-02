# GradlePluginsCore

A collection of gradle plugins meant to codify and streamline project development within an organization.  


## ProjectDefaultsPlugin (project-defaults)

This plugin sets system-level gradle project defaults.  Specifically, java version and memory settings for spawned
processes.  By default, gradle forks processes for both compilation and testing and the memory settings of these spawned
processes default to the settings of the underlying system, which may be very different between dev machine and CI server.

The plugin defaults (jdk, compiler encoding, [min/max heap size, max perm gen size] for compile and test) can be found
[here](https://github.com/BancVue/GradlePluginsCore/blob/master/src/main/groovy/com/bancvue/gradle/ProjectDefaultsProperties.groovy).
If the plugin is applied and nothing more is done, these project defaults will be applied.  Alternative defaults can be
defined through a customized gradle build <TODO: add link>.  Finally, any setting can be overridden on a per-project basis.

In addition, the plugin modifies all jar tasks in the following way...

1. If the project defines a property named 'artifactId', the jar's base name is set to the artifactId
2. The jar's manifest is updated with the following attribute/values...
 * Built-Date - the current date
 * Build-Jdk - the value of the 'java.version' system property


## JavaExtPlugin (java-ext)

The [java](http://www.gradle.org/docs/current/userguide/java_plugin.html)
plugin is applied, adding basic java project support.


#### Added tasks

* sourcesJar, generate a jar with project source files (main.allSource)
* javadocJar, generate a jar with project javadoc (output from the java plugin-provided 'javadoc' task)


## MavenPublishExtPlugin (maven-publish-ext)

The [maven-publish](http://www.gradle.org/docs/current/userguide/publishing_maven.html)
and [maven-publish-auth](https://github.com/sebersole/gradle-maven-publish-auth)
plugins are applied, adding support for dependency resolution and artifact publishing.

The 'repository' [property defaults](https://github.com/BancVue/GradlePluginsCore/blob/master/src/main/groovy/com/bancvue/gradle/maven/MavenRepositoryProperties.groovy)
allow for local maven publication with no configuration.  Organization defaults can be defined to retrieve dependencies
from and publish to a central repository.  This can be used in conjunction with the CustomGradlePlugin to set up a
standard repository configuration for all projects across an organization.  The customization script would look
something like this...

    allprojects {
	    project.group = 'com.organization'
	    project.ext {
            repositoryName = "dev"
            repositoryPublicUrl = "http://organization.com/nexus/content/groups/public"
            repositorySnapshotUrl = "http://organization.com/nexus/content/repositories/snapshots"
            repositoryReleaseUrl = "http://organization.com/nexus/content/repositories/releases"
        }
    }

The plugin adds a maven artifact repository to the project using the configured repositoryName and repositoryPublicUrl.
It also adds a maven publishing repository using the configured repositoryName and either repositorySnapshotUrl
or repositoryReleaseUrl, depending on whether the version contains the string 'SNAPSHOT'.

The plugin also implements a convention-based approach for configuring artifacts to publish.  The only required
parameter is the publication id, a string which is used to derive the various required components like so...

* sourceSet = ${publicationId}; used in archive task generation
* configuration = ${publicatinId}Runtime; used to configure dependencies in the resulting pom
* archiveTask = ${publicationId}Jar; looks for a task with the given name and if not found, creates one from the sourceSet
* sourcesArchiveTask = ${publicationId}SourcesJar; looks for a task with the given name and if not found, creates one from the sourceSet

For example, the following build file excerpt sets up a 'client' configuration and sourceSet.

    group = 'com.example'
    version = '1.0'
    ext.artifactId='service'

    configurations {
        client
    }

    sourceSets {
        main {
            java {
                exclude "com/example/service/client/**"
            }
        }
        client {
            java {
                srcDir 'src/main/java'
                include "com/example/service/client/**"
            }
        }
    }

    publishing_ext {
    	publication("client") {
    }

In this case, two publications would be configured, 'main' (applied by default) and 'client'.

1. publicationId = "main"
 * sourceSet = main
 * configuration = runtime
 * archiveTask = jar
 * sourcesArchiveTask = sourcesJar
 * mavenPath = com.example/service/1.0/service.jar
2. publicationId = client
 * sourceSet = client
 * configuration = clientRuntime
 * archiveTask = jarClient
 * sourcesArchiveTask = sourcesJarClient
 * mavenPath = com.example/service-client/1.0/service-client.jar

See the [integration test](https://github.com/BancVue/GradlePluginsCore/blob/master/src/integrationTest/groovy/com/bancvue/gradle/maven/publish/MavenPublishExtPluginIntegrationSpecification.groovy)
for various example examples usages.


## TestExtPlugin (test-ext)

The [java](http://www.gradle.org/docs/current/userguide/java_plugin.html)
plugin is applied, adding basic java project support.

This plugin adds support for an optional 'mainTest' configuration.  The idea here is to provide an easy mechanism to
publish test-related classes, separate from the main artifact.  For example, this project defines classes to support
gradle plugin testing.  These classes are not appropriate for the main artifact 'gradle-core' since they have
test-related dependencies (e.g. spock), so it publishes those classes in a different artifact 'gradle-core-test'.

If a project directory named src/mainTest (currently hard-coded) is detected, this plugin does the following

* creates configuration 'mainTest'
* creates configuration 'mainTestCompile' which extends from configuration 'compile'
* creates configuration 'mainTestRuntime' which extends from configuration 'runtime'
* creates sourceSet 'mainTest' which includes main.output
* creates javadoc task 'javadocMainTest'
* creates jar task 'jarMainTest'
* creates jar task 'sourcesJarMainTest'
* creates jar task 'javadocJarMainTest'

In addition, the compileClasspath and runtimeClasspath of the 'test' sourceSet are overwritten to include mainTest.output
and the mainTestCompile/mainTestRuntime configurations.  This makes any classes and dependencies defined by mainTest
available to unit tests.


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


## BancvueOssPlugin (bancvue-oss)

With no behavior of it's own, this plugin simply aggregates a number of other plugins which are likely to be needed
on all Bancvue OSS projects.  The following plugins are applied:

* java-ext
* groovy
* project-defaults
* maven-ext
* test-ext
* component-test
* jacoco-ext
* ide-ext
* project-support


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


# Gradle Test Kit

The content of src/mainTest/java/ is a copy of Luke Daley's gradle test kit prototype (https://github.com/alkemist/gradle-test-kit).
There is an open ticket (https://github.com/alkemist/gradle-test-kit/issues/3) for this to be published to central.
If that happens (or better yet, something is released as part of gradle to aid in integration testing), this code
can be removed.

As of this writing, this code is released under the Apache License 2.0, http://www.apache.org/licenses/LICENSE-2.0
