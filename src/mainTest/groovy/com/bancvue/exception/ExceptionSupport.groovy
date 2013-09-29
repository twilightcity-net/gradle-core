package com.bancvue.exception


class ExceptionSupport {

	static Throwable getRootCause(Throwable exception) {
		Throwable result = exception
		while(result.cause != null) {
			result = result.cause
		}
		result
	}
}
