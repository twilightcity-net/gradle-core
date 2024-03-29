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
package net.twilightcity.gradle

import net.twilightcity.gradle.license.LicenseExtPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project


class CoreOssPlugin implements Plugin<Project> {

    static final String PLUGIN_NAME = 'net.twilightcity.core-oss'

    private Project project

    void apply(Project project) {
        this.project = project
        project.pluginManager.apply(CorePlugin)
        project.pluginManager.apply(LicenseExtPlugin)
    }

}
