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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.sv.build.SvAlignedLine

data class SvPort(
    val line: Line,
    val portType: PortType,
    val typeExtracted: SvTypeExtracted,
    val identifier: String
) {

    fun build(): SvAlignedLine {
        return SvAlignedLine(
            line,
            listOf(
                buildPortType(portType, line),
                typeExtracted.identifier,
                typeExtracted.packed,
                identifier,
                typeExtracted.unpacked
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
