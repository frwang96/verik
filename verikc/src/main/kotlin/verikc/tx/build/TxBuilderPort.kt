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

package verikc.tx.build

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.sv.ast.SvPort

object TxBuilderPort {

    fun build(port: SvPort): TxAlignedLine {
        return TxAlignedLine(
            port.property.line,
            listOf(
                buildPortType(port.portType, port.property.line),
                port.property.typeExtracted.identifier,
                port.property.typeExtracted.packed,
                port.property.identifier,
                port.property.typeExtracted.unpacked
            )
        )
    }

    private fun buildPortType(portType: PortType, line: Line): String {
        return when (portType) {
            PortType.INPUT -> "input"
            PortType.OUTPUT -> "output"
            PortType.INOUT -> "inout"
            PortType.BUS -> throw LineException("unable to build port of type bus", line)
            PortType.BUSPORT -> throw LineException("unable to build port of type bus port", line)
        }
    }
}
