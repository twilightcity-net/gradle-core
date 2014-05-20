package com.bancvue.gradle.multiproject
import com.bancvue.gradle.test.AbstractProjectSpecification
import org.gradle.api.Project

class PostEvaluationNotifierTest extends AbstractProjectSpecification {

	private Project subModule

	def setup() {
		subModule = createSubProject("module1")
	}

	@Override
	String getProjectName() {
		return "root"
	}

	private void evaluateAllProjects() {
		project.evaluate()
		subModule.evaluate()
	}

	def "should invoke callback after all projects have been evaluated"() {
		given:
		boolean callbackInvoked = false
		PostEvaluationNotifier notifier = new PostEvaluationNotifier({
			callbackInvoked = true
		})
		notifier.addProject(project)

		when:
		project.evaluate()

		then:
		!callbackInvoked

		when:
		subModule.evaluate()

		then:
		callbackInvoked
	}

	def "should invoke callback once for each project if callback has single argument of type Project"() {
		given:
		List<Project> projectList = []
		PostEvaluationNotifier notifier = new PostEvaluationNotifier({ Project project ->
			projectList.add(project)
		})
		notifier.addProject(project)

		when:
		evaluateAllProjects()

		then:
		projectList.sort() == [project, subModule]
	}

	def "should invoke callback once with list of projects as arguments if callback does not have single argument of type Project"() {
		given:
		def projectList = null
		PostEvaluationNotifier notifier = new PostEvaluationNotifier({ def arg ->
			projectList = arg
		})
		notifier.addProject(project)

		when:
		evaluateAllProjects()

		then:
		projectList instanceof List
		projectList.sort() == [project, subModule]
	}

	def "should include Project only once in callback even if added multiple times"() {
		given:
		List<Project> projectList = null
		PostEvaluationNotifier notifier = new PostEvaluationNotifier({ List<Project> allProjects ->
			projectList = allProjects
		})

		when:
		notifier.addProject(project)
		notifier.addProject(project)
		evaluateAllProjects()

		then:
		projectList.sort() == [project, subModule]
	}

}
