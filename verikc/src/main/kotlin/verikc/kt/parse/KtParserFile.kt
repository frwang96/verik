/*
 * Copyright 2020 Francis Wang
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

package verikc.kt.parse

import verikc.al.AlRule
import verikc.al.AlTree
import verikc.base.ast.LineException
import verikc.base.config.FileConfig
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtImportEntry

object KtParserFile {

    fun parse(kotlinFile: AlTree, fileConfig: FileConfig, symbolContext: SymbolContext): KtFile {
        val packageHeader = kotlinFile.find(AlRule.PACKAGE_HEADER)
        val pkgIdentifier = if (packageHeader.contains(AlRule.IDENTIFIER)) {
            val identifiers = packageHeader
                .find(AlRule.IDENTIFIER)
                .findAll(AlRule.SIMPLE_IDENTIFIER)
                .map { it.unwrap().text }
            identifiers.joinToString(separator = ".")
        } else ""
        if (pkgIdentifier != symbolContext.identifier(fileConfig.pkgSymbol)) {
            throw LineException("package header does not match file path", packageHeader.line)
        }

        val importList = kotlinFile.find(AlRule.IMPORT_LIST)
        val importEntries = importList
            .findAll(AlRule.IMPORT_HEADER)
            .map { KtImportEntry(it) }

        val declarations = kotlinFile
            .findAll(AlRule.TOP_LEVEL_OBJECT)
            .map { it.find(AlRule.DECLARATION) }
            .map { KtDeclaration(it, symbolContext) }

        return KtFile(
            fileConfig,
            importEntries,
            declarations
        )
    }
}
