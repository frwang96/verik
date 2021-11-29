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

import io.verik.importer.ast.element.EAbstractPackage
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.common.ImporterStage
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext

object SourceSerializerStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val packageTextFiles = ArrayList<TextFile>()
        packageTextFiles.addAll(serializePackage(importerContext.compilationUnit.rootPackage, importerContext))
        importerContext.outputContext.packageTextFiles = packageTextFiles
    }

    private fun serializePackage(abstractPackage: EAbstractPackage, importerContext: ImporterContext): List<TextFile> {
        val declarationMap = HashMap<String, ArrayList<EDeclaration>>()
        abstractPackage.declarations.forEach {
            val baseFileName = it.location.path.fileName.toString().substringBefore(".")
            val declarations = declarationMap[baseFileName]
            if (declarations != null) {
                declarations.add(it)
            } else {
                declarationMap[baseFileName] = arrayListOf(it)
            }
        }

        val packagePath = importerContext.config.outputSourceDir.resolve(abstractPackage.name.replace(".", "/"))
        return declarationMap.map { (baseFileName, declarations) ->
            val path = packagePath.resolve("$baseFileName.kt")
            val serializerContext = SerializerContext(importerContext, abstractPackage.name, path)
            declarations.forEach { serializerContext.serializeAsDeclaration(it) }
            serializerContext.getTextFile()
        }
    }
}
