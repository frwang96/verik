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
import verikc.base.SymbolContext
import verikc.base.ast.Line
import verikc.base.ast.Symbol
import verikc.kt.parse.KtParserDeclaration

sealed class KtDeclaration(
    open val line: Line,
    open val identifier: String,
    open val symbol: Symbol
) {

    companion object {

        operator fun invoke(declaration: AlRule, symbolContext: SymbolContext): KtDeclaration {
            return KtParserDeclaration.parse(declaration, symbolContext)
        }
    }
}

sealed class KtType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    open val declarations: List<KtDeclaration>
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val declarations: List<KtDeclaration>,
    val annotations: List<KtAnnotationType>,
    val parameters: List<KtParameterProperty>,
    val constructorInvocation: KtConstructorInvocation,
    val constructorFunction: KtConstructorFunction,
    val objectType: KtObjectType?
): KtType(line, identifier, symbol, declarations)

data class KtObjectType(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val declarations: List<KtDeclaration>,
    val enumProperties: List<KtEnumProperty>?,
    val objectProperty: KtObjectProperty
): KtType(line, identifier, symbol, declarations)

sealed class KtFunction(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    open val parameters: List<KtParameterProperty>,
    open var returnTypeSymbol: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryFunction(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val parameters: List<KtParameterProperty>,
    override var returnTypeSymbol: Symbol?,
    val annotations: List<KtAnnotationFunction>,
    val returnTypeIdentifier: String,
    val block: KtBlock
): KtFunction(line, identifier, symbol, parameters, returnTypeSymbol)

data class KtConstructorFunction(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val parameters: List<KtParameterProperty>,
    override var returnTypeSymbol: Symbol?
): KtFunction(line, identifier, symbol, parameters, returnTypeSymbol)

sealed class KtProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    open var typeSymbol: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val annotations: List<KtAnnotationProperty>,
    val expression: KtExpression
): KtProperty(line, identifier, symbol, typeSymbol)

data class KtObjectProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?
): KtProperty(line, identifier, symbol, typeSymbol)

data class KtParameterProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val typeIdentifier: String,
    val expression: KtExpression?
): KtProperty(line, identifier, symbol, typeSymbol)

data class KtLambdaProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
): KtProperty(line, identifier, symbol, typeSymbol)

data class KtEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val arg: KtExpression?
): KtProperty(line, identifier, symbol, typeSymbol)
