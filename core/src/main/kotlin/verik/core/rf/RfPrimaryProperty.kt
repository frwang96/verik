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

package verik.core.rf

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.SvPrimaryProperty
import verik.core.vk.VkPrimaryProperty

data class RfPrimaryProperty(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val expression: RfExpression
): RfProperty {

    fun extract(symbolTable: RfSymbolTable): SvPrimaryProperty {
        val reifiedType = reifiedType
                ?: throw LineException("primary property has not been reified", this)

        return SvPrimaryProperty(
                line,
                symbolTable.extractType(reifiedType, line),
                identifier
        )
    }

    constructor(primaryProperty: VkPrimaryProperty): this(
            primaryProperty.line,
            primaryProperty.identifier,
            primaryProperty.symbol,
            primaryProperty.type,
            null,
            RfExpression(primaryProperty.expression)
    )
}