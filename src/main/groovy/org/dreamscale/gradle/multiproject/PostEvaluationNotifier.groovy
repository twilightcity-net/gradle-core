/*
 * Copyright 2014 BancVue, LTD
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
package org.dreamscale.gradle.multiproject
import org.gradle.api.Project

import java.util.concurrent.atomic.AtomicInteger

/**
 * NOTE: there has to be a better way to accomplish this...
 *
 * This hack enables multi-project support for certain types of plugins.  Sometimes, a plugin needs
 * to incorporate output from sub-projects.  Code coverage is a good example - ideally, you'd want
 * a coverage report that incorporated a project and any sub-projects rather than one coverage
 * report per project in a multi-project build.  In theory, Project:evaluationDependsOnChildren()
 * could be used to support this but in practice, that does not seem to be the case.  When called
 * during plugin:apply, the sub-projects are evaluated immediately and (it seems) before the same
 * plugin is actually applied to the sub-projects.
 *
 * This hack aims to work around this issue.  A plugin can declare a static instance of this class
 * with a callback passed in during construction.  The callback is then invoked once all projects
 * and sub-projects have been evaluated.
 *
 * WARNING: if a project (or any of it's sub-projects) has already been evaluated, the
 * callback will not fire.
 */
class PostEvaluationNotifier {

	private Map<Project,AtomicInteger> projectToPreEvaluateCounterMap = [:].withDefault { new AtomicInteger(0) }
	private Closure allProjectsEvaluatedCallback

	PostEvaluationNotifier(Closure allProjectsEvaluatedCallback) {
		this.allProjectsEvaluatedCallback = allProjectsEvaluatedCallback
	}

	void addProject(Project project) {
		if (!projectToPreEvaluateCounterMap.containsKey(project)) {
			projectToPreEvaluateCounterMap.get(project).incrementAndGet()

			project.afterEvaluate {
				afterEvaluate(project)
			}

			project.subprojects.each { Project subProject ->
				addProject(subProject)
			}
		}
	}

	private void afterEvaluate(Project project) {
		projectToPreEvaluateCounterMap.get(project).decrementAndGet()

		if (allProjectsEvaluated()) {
			List<Project> allUniqueProjects = projectToPreEvaluateCounterMap.keySet() as List
			invokeCallbackOnAllProjects(allUniqueProjects)
			projectToPreEvaluateCounterMap.clear()
		}
	}

	private boolean allProjectsEvaluated() {
		AtomicInteger nonZeroCounter = projectToPreEvaluateCounterMap.values().find { AtomicInteger counter ->
			counter.get() != 0
		}
		nonZeroCounter == null
	}

	private void invokeCallbackOnAllProjects(List<Project> allUniqueProjects) {
		if (doesCallbackAcceptSingleProjectAsParameter()) {
			allUniqueProjects.each { Project project ->
				allProjectsEvaluatedCallback.call(project)
			}
		} else {
			allProjectsEvaluatedCallback.call(allUniqueProjects)
		}
	}

	private boolean doesCallbackAcceptSingleProjectAsParameter() {
		Class[] parameterTypes = allProjectsEvaluatedCallback.getParameterTypes()
		(parameterTypes.length == 1) && (parameterTypes[0] == Project)
	}

}
