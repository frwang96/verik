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

package verikc.ge.ast

import verikc.base.ast.Line
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkEnumProperty

data class GeEnum(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val typeConstructorFunctionSymbol: Symbol,
    val properties: List<GeEnumProperty>,
    val width: Int
): GeDeclaration {
    constructor(enum: VkEnum): this(
        enum.line,
        enum.identifier,
        enum.symbol,
        enum.typeConstructorFunctionSymbol,
        enum.properties.map { GeEnumProperty(it) },
        enum.width
    )
}

data class GeEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val expression: GeExpressionLiteral
): GeProperty {

    constructor(enumProperty: VkEnumProperty): this(
        enumProperty.line,
        enumProperty.identifier,
        enumProperty.symbol,
        enumProperty.typeSymbol,
        enumProperty.typeSymbol.toTypeGenerifiedInstance(),
        GeExpressionLiteral(enumProperty.expression)
    )
}
