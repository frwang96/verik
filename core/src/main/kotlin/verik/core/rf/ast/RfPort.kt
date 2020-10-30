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

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvPort
import verik.core.sv.ast.SvPortType
import verik.core.vk.ast.VkPort
import verik.core.vk.ast.VkPortType

enum class RfPortType {
    INPUT,
    OUTPUT,
    INOUT,
    BUS,
    BUSPORT;

    fun extract(line: Line): SvPortType {
        return when (this) {
            INPUT -> SvPortType.INPUT
            OUTPUT -> SvPortType.OUTPUT
            else -> throw LineException("unable to extract port type", line)
        }
    }

    companion object {

        operator fun invoke(portType: VkPortType): RfPortType {
            return when (portType) {
                VkPortType.INPUT -> INPUT
                VkPortType.OUTPUT -> OUTPUT
                VkPortType.INOUT -> INOUT
                VkPortType.BUS -> BUS
                VkPortType.BUSPORT -> BUSPORT
            }
        }
    }
}

data class RfPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val portType: RfPortType,
        val expression: RfExpression
): RfProperty {

    fun extract(symbolTable: RfSymbolTable): SvPort {
        val reifiedType = reifiedType
                ?: throw LineException("port has not been reified", this)

        return SvPort(
                line,
                portType.extract(this),
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
            RfPortType(port.portType),
            RfExpression(port.expression)
    )
}
