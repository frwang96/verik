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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.base.Line
import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.kt.parse.KtDeclarationParser

sealed class KtDeclaration(
        override val line: Int,
        open val identifier: String,
        open val symbol: Symbol
): Line {

    companion object {

        operator fun invoke(
                declaration: AlRule,
                file: Symbol,
                symbolContext: SymbolContext
        ): KtDeclaration {
            return KtDeclarationParser.parse(declaration, file, symbolContext)
        }
    }
}

data class KtDeclarationType(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val annotations: List<KtAnnotationType>,
        val parameters: List<KtDeclarationParameter>,
        val constructorInvocation: KtConstructorInvocation,
        val enumEntries: List<KtDeclarationEnumEntry>?,
        val declarations: List<KtDeclaration>
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val annotations: List<KtAnnotationFunction>,
        val parameters: List<KtDeclarationParameter>,
        val body: KtFunctionBody,
        var type: Symbol?
): KtDeclaration(line, identifier, symbol)

sealed class KtDeclarationProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        open var type: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtDeclarationBaseProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val annotations: List<KtAnnotationProperty>,
        val expression: KtExpression
): KtDeclarationProperty(line, identifier, symbol, type)

data class KtDeclarationParameter(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val typeIdentifier: String,
        val expression: KtExpression?
): KtDeclarationProperty(line, identifier, symbol, type)

data class KtDeclarationEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val arg: KtExpression?
): KtDeclarationProperty(line, identifier, symbol, type)
