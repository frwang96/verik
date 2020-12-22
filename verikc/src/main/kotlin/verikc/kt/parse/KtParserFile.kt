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

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.LineException
import verikc.base.config.FileConfig
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtImportEntry

object KtParserFile {

    fun parse(kotlinFile: AlxTree, fileConfig: FileConfig, symbolContext: SymbolContext): KtFile {
        val packageHeader = kotlinFile.find(AlxRuleIndex.PACKAGE_HEADER)
        val pkgIdentifier = if (packageHeader.contains(AlxRuleIndex.IDENTIFIER)) {
            val identifiers = packageHeader
                .find(AlxRuleIndex.IDENTIFIER)
                .findAll(AlxRuleIndex.SIMPLE_IDENTIFIER)
                .map { it.find(AlxTerminalIndex.IDENTIFIER).text!! }
            identifiers.joinToString(separator = ".")
        } else ""
        if (pkgIdentifier != symbolContext.identifier(fileConfig.pkgSymbol)) {
            throw LineException("package header does not match file path", packageHeader.line)
        }

        val importList = kotlinFile.find(AlxRuleIndex.IMPORT_LIST)
        val importEntries = importList
            .findAll(AlxRuleIndex.IMPORT_HEADER)
            .map { KtImportEntry(it) }

        val declarations = kotlinFile
            .findAll(AlxRuleIndex.TOP_LEVEL_OBJECT)
            .map { it.find(AlxRuleIndex.DECLARATION) }
            .map { KtDeclaration(it, symbolContext) }

        return KtFile(
            fileConfig,
            importEntries,
            declarations
        )
    }
}
