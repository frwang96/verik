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

import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

data class KtFile(
        val pkgIdentifier: KtPkgIdentifier,
        val importEntries: List<KtImportEntry>,
        val declarations: List<KtDeclaration>
) {

    companion object {

        operator fun invoke(kotlinFile: AlRule): KtFile {
            val packageHeader = kotlinFile.childAs(AlRuleType.PACKAGE_HEADER)
            val pkgIdentifier = if (packageHeader.containsType(AlRuleType.IDENTIFIER)) {
                val identifiers = packageHeader
                        .childAs(AlRuleType.IDENTIFIER)
                        .childrenAs(AlRuleType.SIMPLE_IDENTIFIER)
                        .map { it.firstAsTokenText() }
                KtPkgIdentifier(identifiers)
            } else KtPkgIdentifier(listOf())

            val importList = kotlinFile.childAs(AlRuleType.IMPORT_LIST)
            val importEntries = importList
                    .childrenAs(AlRuleType.IMPORT_HEADER)
                    .map { KtImportEntry(it) }

            val declarations = kotlinFile
                    .childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
                    .map { it.childAs(AlRuleType.DECLARATION) }
                    .map { KtDeclaration(it) }

            return KtFile(pkgIdentifier, importEntries, declarations)
        }
    }
}