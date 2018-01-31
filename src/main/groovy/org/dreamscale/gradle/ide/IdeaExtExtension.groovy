/*
 * Copyright 2018 DreamScale, Inc
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
package org.dreamscale.gradle.ide

import org.gradle.api.Project


class IdeaExtExtension {

    static final String NAME = "idea_ext"

    static IdeaExtExtension getInstance(Project project) {
        project.extensions.getByName(NAME) as IdeaExtExtension
    }

    Map<String, List> defaultConfigurationVmParameters = [:].withDefault {[]}

    void defaultConfigurationVmParameter(String configurationName, def vmParameter) {
        defaultConfigurationVmParameters[configurationName] << vmParameter
    }

    void applicationConfigurationVmParameter(def vmParameter) {
        defaultConfigurationVmParameter("Application", vmParameter)
    }

    void junitConfigurationVmParameter(def vmParameter) {
        defaultConfigurationVmParameter("JUnit", vmParameter)
    }

    void springBootConfigurationVmParameter(def vmParameter) {
        defaultConfigurationVmParameter("Spring Boot", vmParameter)
    }

    void globalVmParameter(def vmParameter) {
        applicationConfigurationVmParameter(vmParameter)
        springBootConfigurationVmParameter(vmParameter)
        junitConfigurationVmParameter(vmParameter)
    }

}
