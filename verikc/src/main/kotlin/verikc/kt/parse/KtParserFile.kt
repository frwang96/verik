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
import verikc.al.AlRuleType
import verikc.base.SymbolContext
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtImportEntry

object KtParserFile {

    fun parse(kotlinFile: AlRule, pkgSymbol: Symbol, fileSymbol: Symbol, symbolContext: SymbolContext): KtFile {
        val packageHeader = kotlinFile.childAs(AlRuleType.PACKAGE_HEADER)
        val pkgIdentifier = if (packageHeader.containsType(AlRuleType.IDENTIFIER)) {
            val identifiers = packageHeader
                .childAs(AlRuleType.IDENTIFIER)
                .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                .map { it.firstAsTokenText() }
            identifiers.joinToString(separator = ".")
        } else ""
        if (pkgIdentifier != symbolContext.pkgConfig(pkgSymbol).pkgKt) {
            throw LineException("package header does not match file path", packageHeader.line)
        }

        val importList = kotlinFile.childAs(AlRuleType.IMPORT_LIST)
        val importEntries = importList
            .childrenAs(AlRuleType.IMPORT_HEADER)
            .map { KtImportEntry(it) }

        val declarations = kotlinFile
            .childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
            .map { it.childAs(AlRuleType.DECLARATION) }
            .map { KtDeclaration(it, symbolContext) }

        return KtFile(
            fileSymbol,
            importEntries,
            declarations
        )
    }
}
