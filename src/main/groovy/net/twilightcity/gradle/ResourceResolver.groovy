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
package net.twilightcity.gradle

import groovy.util.logging.Slf4j
import net.twilightcity.gradle.resource.ClasspathUrlResolver
import net.twilightcity.gradle.resource.ProjectResourceDirUrlResolver
import net.twilightcity.gradle.resource.ProjectRootUrlResolver
import net.twilightcity.gradle.resource.UrlResolver
import org.gradle.api.GradleException
import org.gradle.api.Project

@Slf4j
class ResourceResolver {

	static ResourceResolver create(Project project) {
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
		URL url = resolverChain.findResult { UrlResolver resolver ->
			resolver.getResourceAsUrlOrNull(resourcePath)
		}
		log.debug("getResourceUrl, resourcePath=${resourcePath}, url=${url}")
		url
	}

	String getResourceContent(String resourcePath) {
		URL url = getResourceURL(resourcePath)
		log.debug("getResourceContent, url=${url}")
		url.text
	}

	void extractResourceToFile(String resourcePath, File targetFile, boolean executable = false) {
		targetFile.parentFile.mkdirs()
		targetFile.text = getResourceContent(resourcePath)
		if (executable) {
			targetFile.executable = true
		}

	}

}
