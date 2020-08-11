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

package io.verik.core.kt

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.kt.resolve.KtSymbolIndexer
import io.verik.core.kt.resolve.KtSymbolMap
import io.verik.core.symbol.FileTableFile
import io.verik.core.symbol.Symbol

data class KtFile(
        val file: Symbol,
        val importEntries: List<KtImportEntry>,
        val declarations: List<KtDeclaration>
) {

    companion object {

        operator fun invoke(
                kotlinFile: AlRule,
                file: FileTableFile,
                symbolMap: KtSymbolMap
        ): KtFile {
            val packageHeader = kotlinFile.childAs(AlRuleType.PACKAGE_HEADER)
            val pkgIdentifier = if (packageHeader.containsType(AlRuleType.IDENTIFIER)) {
                val identifiers = packageHeader
                        .childAs(AlRuleType.IDENTIFIER)
                        .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                        .map { it.firstAsTokenText() }
                identifiers.joinToString(separator = ".")
            } else ""
            if (pkgIdentifier != file.config.pkgKt) {
                throw LineException("package header does not match file path", packageHeader)
            }

            val importList = kotlinFile.childAs(AlRuleType.IMPORT_LIST)
            val importEntries = importList
                    .childrenAs(AlRuleType.IMPORT_HEADER)
                    .map { KtImportEntry(it) }

            val indexer = KtSymbolIndexer(file.symbol)
            val declarations = kotlinFile
                    .childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
                    .map { it.childAs(AlRuleType.DECLARATION) }
                    .map { KtDeclaration(it, symbolMap, indexer) }

            return KtFile(
                    file.symbol,
                    importEntries,
                    declarations
            )
        }
    }
}