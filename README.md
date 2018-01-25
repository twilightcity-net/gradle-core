[![Build Status](https://travis-ci.org/dreamscale-io/gradle-core.png?branch=master)](https://travis-ci.org/dreamscale-io/gradle-core)

# Core Gradle Plugins

A collection of gradle plugins meant to codify and streamline project development within an organization.

See the [wiki](https://github.com/BancVue/gradle-core/wiki) for documentation.

# Publishing to Bintray

Make sure you have a bintray account and are a member of the [dreamscale organization](https://bintray.com/dreamscale/organization/edit)

Open your [user profile](https://bintray.com/profile/edit/organizations) and retrieve your API Key

Execute bintray upload `gw bintrayUpload -Pbintray.user=<bintray user> -Pbintray.key=<api key>`

Open the DreamScale [gradle-core](https://bintray.com/dreamscale/maven-public/org.dreamscale%3Agradle-core) package and
click the [Publish](https://bintray.com/dreamscale/maven-public/org.dreamscale%3Agradle-core/publish) link