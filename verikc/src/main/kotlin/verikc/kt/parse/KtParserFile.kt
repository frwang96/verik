/*
 * Copyright (c) 2020 Francis Wang
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

import verikc.al.ast.AlFile
import verikc.al.ast.AlRule
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserFile {

    fun parse(file: AlFile, symbolContext: SymbolContext): KtFile {
        val packageHeader = file.kotlinFile.find(AlRule.PACKAGE_HEADER)
        val pkgIdentifier = if (packageHeader.contains(AlRule.IDENTIFIER)) {
            val identifiers = packageHeader
                .find(AlRule.IDENTIFIER)
                .findAll(AlRule.SIMPLE_IDENTIFIER)
                .map { it.unwrap().text }
            identifiers.joinToString(separator = ".")
        } else ""
        if (pkgIdentifier != symbolContext.identifier(file.config.pkgSymbol)) {
            throw LineException("package header does not match file path", packageHeader.line)
        }

        val importList = file.kotlinFile.find(AlRule.IMPORT_LIST)
        val importEntries = importList
            .findAll(AlRule.IMPORT_HEADER)
            .map { KtImportEntry(it) }

        val declarations = file.kotlinFile
            .findAll(AlRule.TOP_LEVEL_OBJECT)
            .map { it.find(AlRule.DECLARATION) }

        val types = ArrayList<KtType>()
        val functions = ArrayList<KtFunction>()
        val properties = ArrayList<KtProperty>()
        declarations.forEach {
            when (val declaration = KtParserDeclaration.parse(it, symbolContext)) {
                is KtType -> types.add(declaration)
                is KtFunction -> functions.add(declaration)
                is KtProperty -> properties.add(declaration)
            }
        }

        return KtFile(
            file.config,
            importEntries,
            null,
            types,
            functions,
            properties
        )
    }
}
