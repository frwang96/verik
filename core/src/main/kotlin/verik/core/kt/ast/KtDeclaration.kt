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

package verik.core.kt.ast

import verik.core.al.AlRule
import verik.core.base.ast.Line
import verik.core.base.ast.Symbol
import verik.core.base.SymbolIndexer
import verik.core.kt.parse.KtParserDeclaration

sealed class KtDeclaration(
        override val line: Int,
        open val identifier: String,
        open val symbol: Symbol
): Line {

    companion object {

        operator fun invoke(declaration: AlRule, indexer: SymbolIndexer): KtDeclaration {
            return KtParserDeclaration.parse(declaration, indexer)
        }
    }
}

sealed class KtType(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        open val declarations: List<KtDeclaration>
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryType(
        override val line: Int,
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
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val declarations: List<KtDeclaration>,
        val enumProperties: List<KtEnumProperty>?,
        val objectProperty: KtObjectProperty
): KtType(line, identifier, symbol, declarations)

sealed class KtFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        open val parameters: List<KtParameterProperty>,
        open var returnType: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val parameters: List<KtParameterProperty>,
        override var returnType: Symbol?,
        val annotations: List<KtAnnotationFunction>,
        val body: KtFunctionBody
): KtFunction(line, identifier, symbol, parameters, returnType)

data class KtConstructorFunction(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val parameters: List<KtParameterProperty>,
        override var returnType: Symbol?
): KtFunction(line, identifier, symbol, parameters, returnType)

sealed class KtProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        open var type: Symbol?
): KtDeclaration(line, identifier, symbol)

data class KtPrimaryProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val annotations: List<KtAnnotationProperty>,
        val expression: KtExpression
): KtProperty(line, identifier, symbol, type)

data class KtObjectProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?
): KtProperty(line, identifier, symbol, type)

data class KtParameterProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val typeIdentifier: String,
        val expression: KtExpression?
): KtProperty(line, identifier, symbol, type)

data class KtLambdaProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
): KtProperty(line, identifier, symbol, type)

data class KtEnumProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override var type: Symbol?,
        val arg: KtExpression?
): KtProperty(line, identifier, symbol, type)
