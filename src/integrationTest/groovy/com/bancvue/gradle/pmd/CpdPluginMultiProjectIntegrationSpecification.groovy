package com.bancvue.gradle.pmd
import com.bancvue.exception.ExceptionSupport
import org.gradle.tooling.BuildException

@Mixin(ExceptionSupport)
class CpdPluginMultiProjectIntegrationSpecification extends AbstractCpdPluginIntegrationSpecification {

	private int minTokenCount = 10

	void setup() {
		file("settings.gradle") << "include 'module'"
		buildFile << """
allprojects {
	apply plugin: 'java'

	repositories {
		mavenCentral()
	}
}
"""
	}

	def "should fail if any project contains cpd violation"() {
		given:
		emptyClassFile("src/main/java/bv/SomeClass.java")
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", minTokenCount)
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

allprojects {
	apply plugin: 'cpd'
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

	def "should not fail if a project contains cpd violation but cpd plugin is not applied"() {
		given:
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}
"""

		and:
		emptyClassFile("src/main/java/bv/SomeClass.java")
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", minTokenCount)

		when:
		run("check")

		then:
		notThrown(BuildException)
	}

	def "should fail if duplicate token threshold exceeded in files across projects"() {
		given:
		int halfMinTokenCount = (minTokenCount / 2) as int
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

allprojects {
	apply plugin: 'cpd'
}
"""

		and:
		classFileWithDuplicateTokens("src/main/java/bv/SomeClass.java", halfMinTokenCount)

		when:
		run("check")

		then:
		notThrown(Exception)

		when:
		classFileWithDuplicateTokens("module/src/main/java/bv/ModuleClass.java", halfMinTokenCount)
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

	def "should include files from sub-project sourceSets not yet created when plugin is applied"() {
		given:
		classFileWithDuplicateTokens("module/src/mainTest/java/bv/ModuleClass.java", minTokenCount)
		buildFile << """
apply plugin: 'cpd'

cpd {
    minimumTokenCount ${minTokenCount}
}

subprojects {
	apply plugin: 'cpd'
	apply plugin: 'test-ext' // creates the 'mainTest' source set
}
"""

		when:
		run("check")

		then:
		thrown(BuildException)
		assertDuplicationDetected()
	}

}
