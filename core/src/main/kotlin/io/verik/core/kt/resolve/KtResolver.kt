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

package io.verik.core.kt.resolve

import io.verik.core.kt.*
import io.verik.core.lang.Lang
import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.symbol.Symbol

object KtResolver {

    fun resolveFile(file: KtFile) {
        file.declarations.map { resolveDeclaration(it) }
    }

    fun resolveDeclaration(declaration: KtDeclaration) {
        when (declaration) {
            is KtDeclarationType -> {
                declaration.declarations.forEach { resolveDeclaration(it) }
            }
            is KtDeclarationFunction -> {
                declaration.parameters.forEach { resolveDeclaration(it) }
                declaration.block.statements.forEach { resolveStatement(it) }
                declaration.type = resolveType(declaration.typeIdentifier, declaration)
            }
            is KtDeclarationBaseProperty -> {
                KtExpressionResolver.resolve(declaration.expression)
            }
            is KtDeclarationParameter -> {
                throw LineException("resolving parameter declarations not supported", declaration)
            }
            is KtDeclarationEnumEntry -> {
                throw LineException("resolving enum entries not supported", declaration)
            }
        }
    }

    private fun resolveStatement(statement: KtStatement) {
        KtExpressionResolver.resolve(statement.expression)
    }

    private fun resolveType(identifier: String, line: Line): Symbol {
        return Lang.typeTable.resolve(identifier)
                ?: throw LineException("could not resolve type $identifier", line)
    }
}