/**
 * Copyright 2013 BancVue, LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bancvue.gradle

import com.bancvue.gradle.resource.ClasspathUrlResolver
import com.bancvue.gradle.resource.ProjectResourceDirUrlResolver
import com.bancvue.gradle.resource.ProjectRootUrlResolver
import com.bancvue.gradle.resource.UrlResolver
import org.gradle.api.GradleException
import org.gradle.api.Project

class ResourceResolver {

	public static ResourceResolver create(Project project) {
		List<UrlResolver> resolverChain = [
				new ProjectResourceDirUrlResolver(project),
				new ProjectRootUrlResolver(project),
				new ClasspathUrlResolver()
		]
		new ResourceResolver(resolverChain)
	}


	private List<UrlResolver> resolverChain

	ResourceResolver(List<UrlResolver> resolverChain) {
		this.resolverChain = resolverChain
	}

	public <T> T resolveObjectFromMap(String resourcePath, Class<T> type) {
		String jsonContent = getResourceContent(resourcePath)
		Map result = Eval.me("[${jsonContent}]") as Map
		type.newInstance(result)
	}

	URL getResourceURL(String resourcePath) {
		assertResourcePathNotEmpty(resourcePath)

		URL resourceUrl = getResourceUrlOrNull(resourcePath)
		if (resourceUrl == null) {
			throw new GradleException("Failed to resolve resource with path=${resourcePath}")
		}
		resourceUrl
	}

	private void assertResourcePathNotEmpty(String resourcePath) {
		if (resourcePath == null) {
			throw new GradleException("Invalid input, resourcePath must not be null")
		}
	}

	private URL getResourceUrlOrNull(String resourcePath) {
		resolverChain.findResult { UrlResolver resolver ->
			resolver.getResourceAsUrlOrNull(resourcePath)
		}
	}

	String getResourceContent(String resourcePath) {
		getResourceURL(resourcePath).text
	}

}
