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

package verikc.kt.ast

import verikc.al.AlRule
import verikc.al.AlRuleType
import verikc.al.AlTokenType
import verikc.base.ast.Line
import verikc.base.ast.Symbol

sealed class KtImportEntry(
    open val line: Line,
    open val pkgIdentifier: String,
    open var pkgSymbol: Symbol?
) {

    companion object {

        operator fun invoke(importHeader: AlRule): KtImportEntry {
            val identifiers = importHeader
                .childAs(AlRuleType.IDENTIFIER)
                .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                .map { it.firstAsTokenText() }
            return if (importHeader.containsType(AlTokenType.MULT)) {
                KtImportEntryAll(
                    importHeader.line,
                    identifiers.joinToString(separator = "."),
                    null
                )
            } else {
                KtImportEntryIdentifier(
                    importHeader.line,
                    identifiers.dropLast(1).joinToString(separator = "."),
                    null,
                    identifiers.last()
                )
            }
        }
    }
}

data class KtImportEntryAll(
    override val line: Line,
    override val pkgIdentifier: String,
    override var pkgSymbol: Symbol?
): KtImportEntry(line, pkgIdentifier, pkgSymbol)

data class KtImportEntryIdentifier(
    override val line: Line,
    override val pkgIdentifier: String,
    override var pkgSymbol: Symbol?,
    val identifier: String
): KtImportEntry(line, pkgIdentifier, pkgSymbol)
