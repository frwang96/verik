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

import verik.core.lang.Lang
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangTypeClass
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.sv.SvPort
import verik.core.sv.SvPortType
import verik.core.sv.SvTypeInstance
import verik.core.symbol.Symbol
import verik.core.vk.VkPort
import verik.core.vk.VkPortType

enum class ItPortType {
    INPUT,
    OUTPUT,
    INOUT,
    INTERF,
    MODPORT;

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
                VkPortType.INTERF -> INTERF
                VkPortType.MODPORT -> MODPORT
            }
        }
    }
}

data class ItPort(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val portType: ItPortType,
        val typeInstance: ItTypeInstance
): ItDeclaration {

    fun extract(): SvPort {
        if (typeInstance != ItTypeInstance(TYPE_BOOL, listOf())) {
            throw LineException("port type instance not supported", this)
        }
        return SvPort(
                line,
                portType.extract(this),
                SvTypeInstance("logic", "", ""),
                identifier
        )
    }

    companion object {

        operator fun invoke(port: VkPort): ItPort {
            val expression = ItExpression(port.expression)
            if (Lang.typeTable.typeClass(expression.typeInstance, port.line) != LangTypeClass.TYPE) {
                throw LineException("type expression expected", expression)
            }

            return ItPort(
                    port.line,
                    port.identifier,
                    port.symbol,
                    ItPortType(port.portType),
                    expression.typeInstance
            )
        }
    }
}
