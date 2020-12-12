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

import verikc.base.ast.Line
import verikc.base.ast.Symbol

sealed class KtProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    open var typeSymbol: Symbol?
): KtDeclaration

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
