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

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object SourceSerializer : SerializerStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.files().forEach {
            val sourceOutputFile = serialize(projectContext, it)
            if (sourceOutputFile != null)
                projectContext.outputTextFiles.add(sourceOutputFile)
        }
    }

    private fun serialize(projectContext: ProjectContext, file: EFile): TextFile? {
        if (file.members.isEmpty())
            return null
        val sourceBuilder = SourceBuilder(projectContext, file)
        val declarationSerializationVisitor = DeclarationSerializerVisitor(sourceBuilder)
        file.members.forEach {
            declarationSerializationVisitor.serializeAsDeclaration(it)
        }
        return sourceBuilder.toTextFile()
    }
}
