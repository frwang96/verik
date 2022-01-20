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

package io.verik.importer.serialize.source

import io.verik.importer.ast.kt.element.KtPackage
import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object SourceSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val sourceTextFiles = ArrayList<TextFile>()
        projectContext.project.packages.forEach {
            sourceTextFiles.addAll(serialize(it, projectContext))
        }
        projectContext.outputContext.sourceTextFiles = sourceTextFiles
    }

    private fun serialize(`package`: KtPackage, projectContext: ProjectContext): List<TextFile> {
        return `package`.files.map { file ->
            val serializeContext = SerializeContext(projectContext, `package`.name, file.outputPath)
            file.declarations.forEach { serializeContext.serialize(it) }
            serializeContext.getTextFile()
        }
    }
}
