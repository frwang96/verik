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

import io.verik.core.Line
import io.verik.core.al.AlRule
import io.verik.core.kt.resolve.KtSymbolIndexer
import io.verik.core.kt.resolve.KtSymbolTable
import io.verik.core.symbol.Symbol

sealed class KtDeclaration(
        override val line: Int,
        open val identifier: String,
        open val symbol: Symbol
): Line {

    companion object {

        operator fun invoke(
                declaration: AlRule,
                symbolTable: KtSymbolTable,
                indexer: KtSymbolIndexer
        ): KtDeclaration {
            return KtDeclarationParser.parse(declaration, symbolTable, indexer)
        }
    }
}

data class KtDeclarationType(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val modifiers: List<KtModifier>,
        val parameters: List<KtDeclarationParameter>,
        val constructorInvocation: KtConstructorInvocation,
        val enumEntries: List<KtDeclarationEnumEntry>?,
        val declarations: List<KtDeclaration>
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val modifiers: List<KtModifier>,
        val parameters: List<KtDeclarationParameter>,
        val typeIdentifier: String,
        val block: KtBlock,
        var type: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val modifiers: List<KtModifier>,
        val expression: KtExpression
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationParameter(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val vararg: Boolean,
        val typeIdentifier: String,
        val expression: KtExpression?,
        var type: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val arg: KtExpression?,
        var type: Symbol?
): KtDeclaration(line, identifier, symbol)
