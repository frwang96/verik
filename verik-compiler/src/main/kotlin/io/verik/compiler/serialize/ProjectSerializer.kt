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

package io.verik.compiler.serialize

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.main.m

object ProjectSerializer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val outputTextFiles = ArrayList<TextFile>()

        m.log("Serialize: Serialize config file")
        outputTextFiles.add(ConfigFileSerializer.serialize(projectContext))
        m.flush()

        m.log("Serialize: Serialize order file")
        outputTextFiles.add(OrderFileSerializer.serialize(projectContext))
        m.flush()

        m.log("Serialize: Serialize package files")
        projectContext.project.basicPackages.forEach {
            val packageOutputFile = PackageFileSerializer.serialize(projectContext, it)
            if (packageOutputFile != null)
                outputTextFiles.add(packageOutputFile)
        }
        m.flush()

        m.log("Serialize: Serialize source files")
        projectContext.project.files().forEach {
            val sourceOutputFile = SourceSerializer.serialize(projectContext, it)
            if (sourceOutputFile != null)
                outputTextFiles.add(sourceOutputFile)
        }
        m.flush()

        projectContext.outputTextFiles = outputTextFiles
    }
}