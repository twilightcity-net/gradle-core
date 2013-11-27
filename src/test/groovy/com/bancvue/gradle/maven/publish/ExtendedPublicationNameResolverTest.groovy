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

import org.junit.Test

class ExtendedPublicationNameResolverTest {

	private ExtendedPublicationNameResolver resolver

	@Test
	void getPublicationIdAppendix_ShouldReturnEmptyString_IfIdIsMain() {
		resolver = new ExtendedPublicationNameResolver("main")

		assert resolver.getPublicationIdAppendix() == ""
	}

	@Test
	void getPublicationIdAppendix_ShouldStripMain_IfIdStartsWithMain() {
		resolver = new ExtendedPublicationNameResolver("mainTest")

		assert resolver.getPublicationIdAppendix() == "test"
	}

	@Test
	void getPublicationIdAppendix_ShouldReturnPublicationId_IfIdDoesNotStartWithMain() {
		resolver = new ExtendedPublicationNameResolver("isnotmain")

		assert resolver.getPublicationIdAppendix() == "isnotmain"
	}

	@Test
	void getArtifactIdAppendix_ShouldDeCamelCasePublicationId() {
		resolver = new ExtendedPublicationNameResolver("idWithCamelCase")

		assert resolver.getArtifactIdAppendix() == "id-with-camel-case"
	}

}
