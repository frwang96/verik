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

package verikc.rf.ast

import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.base.ast.TypeReified
import verikc.vk.ast.VkPrimaryProperty

data class RfPrimaryProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val expression: RfExpression
): RfProperty {

    constructor(primaryProperty: VkPrimaryProperty): this(
        primaryProperty.line,
        primaryProperty.identifier,
        primaryProperty.symbol,
        primaryProperty.typeSymbol,
        null,
        RfExpression(primaryProperty.expression)
    )
}
