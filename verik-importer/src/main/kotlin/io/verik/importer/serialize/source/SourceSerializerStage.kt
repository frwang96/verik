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

import io.verik.importer.ast.sv.element.SvCompilationUnit
import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object SourceSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val sourceTextFiles = ArrayList<TextFile>()
        sourceTextFiles.addAll(serialize(projectContext.compilationUnit, projectContext))
        projectContext.outputContext.sourceTextFiles = sourceTextFiles
    }

    private fun serialize(compilationUnit: SvCompilationUnit, projectContext: ProjectContext): List<TextFile> {
        val declarationMap = HashMap<String, ArrayList<SvDeclaration>>()
        compilationUnit.declarations.forEach {
            val baseFileName = it.location.path.fileName.toString().substringBefore(".")
            val declarations = declarationMap[baseFileName]
            if (declarations != null) {
                declarations.add(it)
            } else {
                declarationMap[baseFileName] = arrayListOf(it)
            }
        }

        val packagePath = projectContext.config.outputSourceDir.resolve("imported")
        return declarationMap.map { (baseFileName, declarations) ->
            val path = packagePath.resolve("$baseFileName.kt")
            val serializeContext = SerializeContext(projectContext, "imported", path)
            declarations.forEach { serializeContext.serialize(it) }
            serializeContext.getTextFile()
        }
    }
}
