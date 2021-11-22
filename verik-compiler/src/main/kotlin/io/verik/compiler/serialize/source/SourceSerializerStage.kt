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
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext

object SourceSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val basicPackageSourceTextFiles = ArrayList<TextFile>()
        val basicPackageSourceTextFilesLabeled = ArrayList<TextFile>()
        projectContext.project.basicPackages.forEach { basicPackage ->
            basicPackage.files.forEach {
                if (!it.isEmptySerialization()) {
                    val sourceBuilderResult = serialize(projectContext, it)
                    basicPackageSourceTextFiles.add(sourceBuilderResult.textFile)
                    if (sourceBuilderResult.textFileLabeled != null)
                        basicPackageSourceTextFilesLabeled.add(sourceBuilderResult.textFileLabeled)
                }
            }
        }
        projectContext.outputContext.basicPackageSourceTextFiles = basicPackageSourceTextFiles
        projectContext.outputContext.basicPackageSourceTextFilesLabeled = basicPackageSourceTextFilesLabeled

        val rootPackageSourceTextFiles = ArrayList<TextFile>()
        val rootPackageSourceTextFilesLabeled = ArrayList<TextFile>()
        projectContext.project.rootPackage.files.forEach {
            if (!it.isEmptySerialization()) {
                val sourceBuilderResult = serialize(projectContext, it)
                rootPackageSourceTextFiles.add(sourceBuilderResult.textFile)
                if (sourceBuilderResult.textFileLabeled != null)
                    rootPackageSourceTextFilesLabeled.add(sourceBuilderResult.textFileLabeled)
            }
        }
        projectContext.outputContext.rootPackageSourceTextFiles = rootPackageSourceTextFiles
        projectContext.outputContext.rootPackageSourceTextFilesLabeled = rootPackageSourceTextFilesLabeled
    }

    private fun serialize(projectContext: ProjectContext, file: EFile): SourceBuilderResult {
        val serializerContext = SerializerContext(projectContext, file)
        file.declarations.forEach {
            serializerContext.serializeAsDeclaration(it)
        }
        return serializerContext.getSourceBuilderResult()
    }
}
