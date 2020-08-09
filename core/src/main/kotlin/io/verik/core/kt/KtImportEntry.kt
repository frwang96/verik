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

import io.verik.core.FileLine
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlTokenType

sealed class KtImportEntry(
        open val pkgIdentifier: KtPkgIdentifier,
        open val fileLine: FileLine
) {

    companion object {

        operator fun invoke(importHeader: AlRule): KtImportEntry {
            val identifiers = importHeader
                    .childAs(AlRuleType.IDENTIFIER)
                    .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .map { it.firstAsTokenText() }
            return if (importHeader.containsType(AlTokenType.MULT)) {
                KtImportEntryAll(
                        KtPkgIdentifier(identifiers),
                        importHeader.fileLine
                )
            } else {
                KtImportEntryIdentifier(
                        KtPkgIdentifier(identifiers.dropLast(1)),
                        importHeader.fileLine,
                        identifiers.last()
                )
            }
        }
    }
}

data class KtImportEntryAll(
        override val pkgIdentifier: KtPkgIdentifier,
        override val fileLine: FileLine
): KtImportEntry(pkgIdentifier, fileLine)

data class KtImportEntryIdentifier(
        override val pkgIdentifier: KtPkgIdentifier,
        override val fileLine: FileLine,
        val identifier: String
): KtImportEntry(pkgIdentifier, fileLine)
