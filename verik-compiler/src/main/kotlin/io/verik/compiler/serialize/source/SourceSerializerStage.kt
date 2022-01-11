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
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object SourceSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val regularPackageTextFiles = ArrayList<TextFile>()
        projectContext.project.nativeRegularPackages.forEach { regularPackage ->
            regularPackage.files.forEach {
                if (!it.isEmptySerialization()) {
                    regularPackageTextFiles.add(serialize(projectContext, it))
                }
            }
        }
        projectContext.outputContext.regularPackageTextFiles = regularPackageTextFiles

        val rootPackageTextFiles = ArrayList<TextFile>()
        projectContext.project.nativeRootPackage.files.forEach {
            if (!it.isEmptySerialization()) {
                rootPackageTextFiles.add(serialize(projectContext, it))
            }
        }
        projectContext.outputContext.rootPackageTextFiles = rootPackageTextFiles
    }

    private fun serialize(projectContext: ProjectContext, file: EFile): TextFile {
        val serializeContext = SerializeContext(file)
        file.declarations.forEach {
            serializeContext.serializeAsDeclaration(it)
        }
        val sourceActionLines = serializeContext.getSourceActionLines()
        val sourceBuilder = SourceBuilder(projectContext, file)
        sourceBuilder.build(sourceActionLines)
        return sourceBuilder.toTextFile()
    }
}
