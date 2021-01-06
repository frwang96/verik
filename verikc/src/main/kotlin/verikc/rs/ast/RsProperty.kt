/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.rs.ast

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtEnumProperty
import verikc.kt.ast.KtParameterProperty
import verikc.kt.ast.KtPrimaryProperty
import verikc.kt.ast.KtProperty

sealed class RsProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    open var typeSymbol: Symbol?
): RsDeclaration {

    companion object {

        operator fun invoke(property: KtProperty): RsProperty {
            return when (property) {
                is KtPrimaryProperty -> RsPrimaryProperty(property)
                is KtParameterProperty -> RsParameterProperty(property)
                is KtEnumProperty -> RsEnumProperty(property)
            }
        }
    }
}

data class RsPrimaryProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val annotations: List<AnnotationProperty>,
    val typeIdentifier: String?,
    val expression: RsExpression?
): RsProperty(line, identifier, symbol, typeSymbol) {

    constructor(primaryProperty: KtPrimaryProperty): this(
        primaryProperty.line,
        primaryProperty.identifier,
        primaryProperty.symbol,
        null,
        primaryProperty.annotations,
        primaryProperty.typeIdentifier,
        primaryProperty.expression?.let { RsExpression(it) }
    )
}

data class RsParameterProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val typeIdentifier: String,
    val expression: RsExpression?
): RsProperty(line, identifier, symbol, typeSymbol) {

    constructor(parameterProperty: KtParameterProperty): this(
        parameterProperty.line,
        parameterProperty.identifier,
        parameterProperty.symbol,
        null,
        parameterProperty.typeIdentifier,
        parameterProperty.expression?.let { RsExpression(it) }
    )
}

data class RsEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override var typeSymbol: Symbol?,
    val arg: RsExpression?
): RsProperty(line, identifier, symbol, typeSymbol) {

    constructor(enumProperty: KtEnumProperty): this(
        enumProperty.line,
        enumProperty.identifier,
        enumProperty.symbol,
        null,
        enumProperty.arg?.let { RsExpression(it) }
    )
}
