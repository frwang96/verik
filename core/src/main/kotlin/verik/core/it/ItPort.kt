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

package verik.core.it

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.sv.SvPort
import verik.core.sv.SvPortType
import verik.core.vk.VkPort
import verik.core.vk.VkPortType

enum class ItPortType {
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

        operator fun invoke(portType: VkPortType): ItPortType {
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

data class ItPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val portType: ItPortType,
        val expression: ItExpression
): ItProperty {

    fun extract(): SvPort {
        val typeReified = typeReified
                ?: throw LineException("port has not been reified", this)

        return SvPort(
                line,
                portType.extract(this),
                typeReified.extract(this),
                identifier
        )
    }

    constructor(port: VkPort): this(
            port.line,
            port.identifier,
            port.symbol,
            port.type,
            null,
            ItPortType(port.portType),
            ItExpression(port.expression)
    )
}
