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

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import java.nio.file.FileSystems
import java.nio.file.Paths

object FileChecker : PreCheckerStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.ktFiles.forEach {
            val path = Paths.get(it.virtualFilePath)
            if (path.fileName == Paths.get("Pkg.kt"))
                Messages.FILE_NAME_RESERVED.on(it, path.fileName)

            val packageName = it.packageFqName.asString()
            val relativePath = packageName.replace(".", FileSystems.getDefault().separator)
            val packagePath = projectContext.config.inputSourceDir.resolve(relativePath)
            if (path.parent != packagePath)
                Messages.FILE_LOCATION_MISMATCH.on(it)
        }
    }
}
