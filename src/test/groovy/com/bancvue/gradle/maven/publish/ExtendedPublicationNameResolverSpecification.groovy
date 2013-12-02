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
package com.bancvue.gradle.maven.publish

import spock.lang.Specification

class ExtendedPublicationNameResolverSpecification extends Specification {

	private ExtendedPublicationNameResolver resolver

	def "getPublicationIdAppendix should return empty string if id is main"() {
		when:
		resolver = new ExtendedPublicationNameResolver("main")

		then:
		assert resolver.getPublicationIdAppendix() == ""
	}

	def "getPublicationIdAppendix should strip main if id starts with main"() {
		when:
		resolver = new ExtendedPublicationNameResolver("mainTest")

		then:
		resolver.getPublicationIdAppendix() == "test"
	}

	def "getPublicationIdAppendix should return publication id if id does not start with main"() {
		when:
		resolver = new ExtendedPublicationNameResolver("isnotmain")

		then:
		resolver.getPublicationIdAppendix() == "isnotmain"
	}

	def "getArtifactIdAppendix should de-camel case publication id"() {
		when:
		resolver = new ExtendedPublicationNameResolver("idWithCamelCase")

		then:
		resolver.getArtifactIdAppendix() == "id-with-camel-case"
	}

}
