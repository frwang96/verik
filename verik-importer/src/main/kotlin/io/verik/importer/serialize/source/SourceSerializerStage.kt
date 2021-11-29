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

import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.common.ImporterStage
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

object SourceSerializerStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val declarationsMap = HashMap<Path, ArrayList<EDeclaration>>()
        importerContext.compilationUnit.declarations.forEach {
            val outputPath = getOutputPath(importerContext, it.location)
            val declarations = declarationsMap[outputPath]
            if (declarations != null) {
                declarations.add(it)
            } else {
                declarationsMap[outputPath] = arrayListOf(it)
            }
        }

        val rootPackageTextFiles = ArrayList<TextFile>()
        declarationsMap.forEach { (path, declarations) ->
            rootPackageTextFiles.add(serialize(importerContext, path, declarations))
        }
        importerContext.outputContext.rootPackageTextFiles = rootPackageTextFiles
    }

    private fun getOutputPath(importerContext: ImporterContext, location: SourceLocation): Path {
        val name = location.path.fileName.toString().substringBefore(".")
        return importerContext.config.outputSourceDir.resolve("$name.kt")
    }

    private fun serialize(importerContext: ImporterContext, path: Path, declarations: List<EDeclaration>): TextFile {
        val serializerContext = SerializerContext(importerContext, path)
        declarations.forEach { serializerContext.serializeAsDeclaration(it) }
        return serializerContext.getTextFile()
    }
}
