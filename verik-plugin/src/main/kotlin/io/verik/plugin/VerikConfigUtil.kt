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

package io.verik.plugin

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object VerikConfigUtil {

    fun getVersion(project: Project): String {
        val configuration = project.configurations.getByName("compileClasspath")
        configuration.allDependencies.forEach {
            if (it.group == "io.verik" && it.name == "verik-core")
                return it.version.toString()
        }
        throw GradleException("Verik configuration failed: Could not determine version number")
    }

    fun getTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))
    }
}
