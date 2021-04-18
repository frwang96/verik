/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.verikxc.main

import org.gradle.api.Plugin
import org.gradle.api.Project

const val VERSION = "1.0"

@Suppress("unused")
class VerikxcPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.create("verikxc") {
            // TODO use plugin extension
            // val verikxcPluginExtension = project.extensions.create("verikxc", VerikxcPluginExtension::class.java)
            it.doLast {
                println()
                println("VERIKXC $VERSION")
            }
        }
    }
}