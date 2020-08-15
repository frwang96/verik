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

package verik.core.kt.resolve

import verik.core.kt.*
import verik.core.lang.Lang
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.symbol.Symbol

object KtFunctionResolver {

    fun resolveFile(file: KtFile) {
        file.declarations.forEach { resolveDeclaration(it) }
    }

    fun resolveDeclaration(declaration: KtDeclaration) {
        when (declaration) {
            is KtDeclarationType -> {
                declaration.declarations.forEach { resolveDeclaration(it) }
            }
            is KtDeclarationFunction -> {
                resolveFunction(declaration)
            }
            is KtDeclarationBaseProperty -> {}
            is KtDeclarationParameter -> {}
            is KtDeclarationEnumEntry -> {}
        }
    }

    private fun resolveFunction(function: KtDeclarationFunction) {
        function.type = resolveType(function.typeIdentifier, function)
    }

    private fun resolveType(identifier: String, line: Line): Symbol {
        return Lang.typeTable.resolve(identifier)
                ?: throw LineException("could not resolve type $identifier", line)
    }
}