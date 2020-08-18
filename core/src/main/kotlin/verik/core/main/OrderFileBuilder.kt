/*
 * Copyright 2020 Francis Wang
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

package verik.core.main

import verik.core.main.config.ProjectConfig

object OrderFileBuilder {

    fun build(projectConfig: ProjectConfig): String {
        val builder = StringBuilder()
        builder.appendLine(projectConfig.compile.top)
        for (pkg in projectConfig.symbolContext.pkgs()) {
            for (file in projectConfig.symbolContext.files(pkg)) {
                val fileConfig = projectConfig.symbolContext.fileConfig(file)
                builder.appendLine(fileConfig.outFile.relativeTo(projectConfig.buildOutDir))
            }
        }
        return builder.toString()
    }
}