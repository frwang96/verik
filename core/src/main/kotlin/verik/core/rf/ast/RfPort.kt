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

package verik.core.rf.ast

import verik.core.base.ast.LineException
import verik.core.base.ast.PortType
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvPort
import verik.core.vk.ast.VkPort

data class RfPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var reifiedType: ReifiedType?,
        val portType: PortType,
        val expression: RfExpression
): RfProperty {

    fun extract(symbolTable: RfSymbolTable): SvPort {
        val reifiedType = reifiedType
                ?: throw LineException("port has not been reified", this)

        return SvPort(
                line,
                portType,
                symbolTable.extractType(reifiedType, line),
                identifier
        )
    }

    constructor(port: VkPort): this(
            port.line,
            port.identifier,
            port.symbol,
            port.type,
            null,
            port.portType,
            RfExpression(port.expression)
    )
}
