/*
 * Copyright 2021 TwilightCity, Inc
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
package net.twilightcity.gradle.resource

import spock.lang.Specification

class ClasspathUrlResolverSpecification extends Specification {

	ClasspathUrlResolver resolver = new ClasspathUrlResolver()

	def "getResourceAsUrlOrNull should return URL if resource exists on classpath"() {
		given:
		String classpathResourcePath = getClasspathResourcePath()

		expect:
		resolver.getResourceAsUrlOrNull(classpathResourcePath)
	}

	def "getResourceAsUrlOrNull should return null if resource does not exist on classpath"() {
		given:
		String classpathResourcePath = "does_not_exist"

		expect:
		resolver.getResourceAsUrlOrNull(classpathResourcePath) == null
	}

	private static String getClasspathResourcePath() {
		ClasspathUrlResolverSpecification.class.getName().replace(".", "/") + ".class"
	}

}
