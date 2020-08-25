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

package verik.core.kt.parse

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.base.SymbolIndexer
import verik.core.kt.KtDeclaration
import verik.core.kt.KtFile
import verik.core.kt.KtImportEntry

object KtFileParser {

    fun parse(kotlinFile: AlRule, file: Symbol, symbolContext: SymbolContext): KtFile {
        val packageHeader = kotlinFile.childAs(AlRuleType.PACKAGE_HEADER)
        val pkgIdentifier = if (packageHeader.containsType(AlRuleType.IDENTIFIER)) {
            val identifiers = packageHeader
                    .childAs(AlRuleType.IDENTIFIER)
                    .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .map { it.firstAsTokenText() }
            identifiers.joinToString(separator = ".")
        } else ""
        if (pkgIdentifier != symbolContext.pkgConfig(file).pkgKt) {
            throw LineException("package header does not match file path", packageHeader)
        }

        val importList = kotlinFile.childAs(AlRuleType.IMPORT_LIST)
        val importEntries = importList
                .childrenAs(AlRuleType.IMPORT_HEADER)
                .map { KtImportEntry(it) }

        val indexer = SymbolIndexer(file, symbolContext)
        val declarations = kotlinFile
                .childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
                .map { it.childAs(AlRuleType.DECLARATION) }
                .map { KtDeclaration(it, indexer) }

        return KtFile(
                file,
                importEntries,
                declarations
        )
    }
}