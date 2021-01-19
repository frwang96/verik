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

import verikc.base.ast.LineException
import verikc.base.ast.PortType
import verikc.sv.ast.SvConnection

object TxBuilderConnection {

    fun buildConnection(connection: SvConnection): TxAlignedLine {
        return TxAlignedLine(
            connection.line,
            listOf(".${connection.portIdentifier}", "(${TxBuilderExpressionSimple.build(connection.expression)})")
        )
    }

    fun buildPort(connection: SvConnection): TxAlignedLine {
        val portType = when (connection.portType) {
            PortType.INPUT -> "input"
            PortType.OUTPUT -> "output"
            PortType.INOUT -> "inout"
            PortType.BUS, PortType.BUSPORT -> throw LineException("illegal connection port type", connection.line)
        }
        return TxAlignedLine(connection.line, listOf(portType, connection.portIdentifier))
    }
}
