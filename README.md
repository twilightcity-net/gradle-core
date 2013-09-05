# Gradle Test Kit

The content of src/mainTest/java/ is a copy of Luke Daley's gradle test kit prototype (https://github.com/alkemist/gradle-test-kit).
There is an open ticket (https://github.com/alkemist/gradle-test-kit/issues/3) for this to be published to central.
If that happens (or better yet, something is released as part of gradle to aid in integration testing), this code
can be removed.

As of this writing, this code is released under the Apache License 2.0, http://www.apache.org/licenses/LICENSE-2.0


# BancvueProjectPlugin (bancvue)

The 'root' of the Bancvue plugin heirarchy, this plugin defines behavior all Bancvue projects will likely need.

The [java](http://www.gradle.org/docs/current/userguide/java_plugin.html)
and [groovy](http://www.gradle.org/docs/current/userguide/groovy_plugin.html) plugins are applied first.

All other behavior is split into individual plugins covered below.  Each of these plugins (except integration test)
is applied as part of the BancvuePlugin.

## BancvueDefaultsPlugin (bancvue-defaults)


## MavenPublishExtPlugin (maven-publish-ext)

The [maven-publish](http://www.gradle.org/docs/current/userguide/publishing_maven.html)
and [maven-publish-auth](https://github.com/sebersole/gradle-maven-publish-auth)
plugins are applied, adding support for dependency resolution and artifact publishing.


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


## ComponentTestPlugin (component-test)


## IntegrationTestPlugin (integration-test)


## IdeExtPlugin (ide-ext)

The [idea](http://www.gradle.org/docs/current/userguide/idea_plugin.html)
and [eclipse](http://www.gradle.org/docs/current/userguide/idea_plugin.html)
plugins are applied, adding support for generating IDE project files for both IDEA and Eclipse.

#### Added tasks

* refreshIdea, wipe out and regenerate the IDEA project, workspace and module files
* refreshIdeaModule, wipe out and regenerate the IDEA module file
* refreshEclipse, wipe out and regenerate the Eclipse classpath and project files


## ProjectSupportPlugin (project-support)


## CustomGradlePlugin (custom-gradle)

A plugin for generating a custom gradle build which can then be used to codify certain
attributes (e.g. artifact repository urls) or behavior (e.g. default plugins) for an
organization.  The idea is to provide a gradle wrapper customized to an organization
which can used for all internal projects.

<pre><code>
group='com.bancvue'
ext {
    repositoryName = 'nexus'
    repositoryPublicUrl = 'http://internal.domain/nexus/content/groups/public'
    repositorySnapshotUrl = 'http://internal.domain/nexus/content/repositories/snapshots'
    repositoryReleaseUrl = 'http://internal.domain/nexus/content/repositories/releases'

    customGradleBaseVersion = "1.7"
    customGradleVersion = "${customGradleBaseVersion}-bv.1.0"
    customGradleGroupName = "${group}"
    customGradleArtifactId = "gradle-bancvue"
}

apply plugin: 'custom-gradle'
</code></pre>