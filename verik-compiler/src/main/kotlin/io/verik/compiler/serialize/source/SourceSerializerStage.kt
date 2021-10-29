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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile

object SourceSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val basicPackageSourceTextFiles = ArrayList<TextFile>()
        projectContext.project.basicPackages.forEach { basicPackage ->
            basicPackage.files.forEach {
                val sourceOutputFile = serialize(projectContext, it)
                if (sourceOutputFile != null)
                    basicPackageSourceTextFiles.add(sourceOutputFile)
            }
        }
        projectContext.outputContext.basicPackageSourceTextFiles = basicPackageSourceTextFiles

        val rootPackageSourceTextFiles = ArrayList<TextFile>()
        projectContext.project.rootPackage.files.forEach {
            val sourceOutputFile = serialize(projectContext, it)
            if (sourceOutputFile != null)
                rootPackageSourceTextFiles.add(sourceOutputFile)
        }
        projectContext.outputContext.rootPackageSourceTextFiles = rootPackageSourceTextFiles
    }

    private fun serialize(projectContext: ProjectContext, file: EFile): TextFile? {
        if (file.declarations.all { SourceSerializerVisitor.declarationIsHidden(it) })
            return null
        val serializerContext = SerializerContext(projectContext, file)
        file.declarations.forEach {
            serializerContext.serializeAsDeclaration(it)
        }
        return serializerContext.toTextFile()
    }
}
