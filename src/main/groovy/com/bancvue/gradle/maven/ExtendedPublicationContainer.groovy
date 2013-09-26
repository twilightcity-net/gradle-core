/**
 * Copyright 2013 BancVue, LTD
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
package com.bancvue.gradle.maven


class ExtendedPublicationContainer {

	private Map publicationMap = [:]

	List<ExtendedPublication> getExtendedPublications() {
		publicationMap.values() as List
	}

	ExtendedPublication getExtendedPublication(String publicationName) {
		publicationMap[publicationName]
	}

	void capture(Closure publication) {
		publication.delegate = this
		publication.resolveStrategy = Closure.DELEGATE_FIRST
		publication()
	}

	def methodMissing(String name, args) {
		Closure closure = resolveClosureFromArgs(args)
		publicationMap.put(name, new ExtendedPublication(name: name, closure: closure))
	}

	private static Closure resolveClosureFromArgs(args) {
		List argList = args as List

		if ((argList.size() != 1) || !(argList[0] instanceof Closure)) {
			throw new RuntimeException("Expecting closure as only argument, got=${args}")
		}
		argList[0]
	}

}
