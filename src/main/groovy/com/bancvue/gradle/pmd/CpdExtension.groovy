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
package com.bancvue.gradle.pmd

import org.gradle.api.plugins.quality.CodeQualityExtension

class CpdExtension extends CodeQualityExtension {

	static final String NAME = 'cpd'

	int minimumTokenCount = 50

	boolean ignoreLiterals = false

	boolean ignoreIdentifiers = false

	String cpdXsltPath = "pmd/cpdhtml.xslt"

	/**
	 * If true (the default), a task named 'cpdAll' will be created at the root project level and all
	 * other CPD tasks will be disabled.  This task will operate on all java sources across any project
	 * which applies the 'cpd' plugin.
	 * NOTE: though the CPD plugin can be defined on any project within a multi-project build, this
	 * particular setting will only be recognized when defined on the root project.
	 */
	boolean createAllReport = true

}