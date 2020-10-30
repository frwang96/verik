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

package verik.core.rf.check

import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.ast.ConnectionType
import verik.core.base.ast.LineException
import verik.core.base.ast.PortType
import verik.core.base.ast.Symbol
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.rf.RfUtil
import verik.core.rf.ast.RfComponentInstance
import verik.core.rf.ast.RfConnection
import verik.core.rf.ast.RfModule
import verik.core.rf.ast.RfPort
import verik.core.rf.symbol.RfSymbolTable

internal class RfCheckerConnectionTest {

    @Test
    fun `connection duplicate`() {
        val symbolTable = RfSymbolTable()
        symbolTable.addComponent(RfModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = RfComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                null,
                listOf(
                        RfConnection(0, Symbol(1, 1, 3), Symbol(1, 1, 4), ConnectionType.INPUT),
                        RfConnection(0, Symbol(1, 1, 3), Symbol(1, 1, 4), ConnectionType.INPUT)
                )
        )
        assertThrowsMessage<LineException>("duplicate connection [[1, 1, 3]]") {
            RfCheckerConnection.checkComponentInstance(componentInstance, symbolTable)
        }
    }

    @Test
    fun `connections invalid`() {
        val symbolTable = RfSymbolTable()
        symbolTable.addComponent(RfModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = RfComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                null,
                listOf(
                        RfConnection(0, Symbol(1, 1, 3), Symbol(1, 1, 4), ConnectionType.INPUT),
                        RfConnection(0, Symbol(1, 1, 5), Symbol(1, 1, 6), ConnectionType.INPUT)
                )
        )
        assertThrowsMessage<LineException>("invalid connections [[1, 1, 3]], [[1, 1, 5]]") {
            RfCheckerConnection.checkComponentInstance(componentInstance, symbolTable)
        }
    }

    @Test
    fun `connection missing`() {
        val symbolTable = RfSymbolTable()
        symbolTable.addComponent(RfModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(
                        RfPort(0, "a", Symbol(1, 1, 2), TYPE_BOOL, null, PortType.INPUT, RfUtil.EXPRESSION_NULL),
                        RfPort(0, "b", Symbol(1, 1, 3), TYPE_BOOL, null, PortType.INPUT, RfUtil.EXPRESSION_NULL)
                ),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = RfComponentInstance(
                0,
                "m",
                Symbol(1, 1, 4),
                Symbol(1, 1, 1),
                null,
                listOf()
        )
        assertThrowsMessage<LineException>("missing connections [[1, 1, 2]], [[1, 1, 3]]") {
            RfCheckerConnection.checkComponentInstance(componentInstance, symbolTable)
        }
    }

    @Test
    fun `connection valid`() {
        val symbolTable = RfSymbolTable()
        symbolTable.addComponent(RfModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(
                        RfPort(0, "a", Symbol(1, 1, 2), TYPE_BOOL, null, PortType.INPUT, RfUtil.EXPRESSION_NULL),
                ),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = RfComponentInstance(
                0,
                "m",
                Symbol(1, 1, 4),
                Symbol(1, 1, 1),
                null,
                listOf(RfConnection(0, Symbol(1, 1, 2), Symbol(1, 1, 3), ConnectionType.INPUT))
        )
        RfCheckerConnection.checkComponentInstance(componentInstance, symbolTable)
    }

    @Test
    fun `connection type mismatch`() {
        val symbolTable = RfSymbolTable()
        symbolTable.addComponent(RfModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(
                        RfPort(0, "a", Symbol(1, 1, 2), TYPE_BOOL, null, PortType.INPUT, RfUtil.EXPRESSION_NULL),
                ),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = RfComponentInstance(
                0,
                "m",
                Symbol(1, 1, 4),
                Symbol(1, 1, 1),
                null,
                listOf(RfConnection(0, Symbol(1, 1, 2), Symbol(1, 1, 3), ConnectionType.OUTPUT))
        )
        assertThrowsMessage<LineException>("input assignment expected for [[1, 1, 2]]") {
            RfCheckerConnection.checkComponentInstance(componentInstance, symbolTable)
        }
    }
}