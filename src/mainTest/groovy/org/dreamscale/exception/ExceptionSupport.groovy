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
package org.dreamscale.exception


trait ExceptionSupport {

	Throwable getRootCause(Throwable exception) {
		Throwable result = exception
		while(result.cause != null) {
			result = result.cause
		}
		result
	}

	/*
	why this is here...

	In one of our internal integration tests, we are getting a PlaceholderException as the root cause of a BuildException
	when we throw a custom RuntimeException in the build, but we get a ClassCastException when trying to cast it to
	PlaceholderException. In order to verify that our correct internal exception is thrown, we have to inspect the
	exceptionClassName on the PlaceholderException.
	 */
	Throwable getRootCauseAndVerifyPlaceholderExceptionClassName(Throwable exception, Class exceptionClass) {
		Throwable cause = getRootCause(exception)
		assert cause.getExceptionClassName() == exceptionClass.name
		cause
	}
}
