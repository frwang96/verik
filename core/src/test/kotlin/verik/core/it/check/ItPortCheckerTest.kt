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

package verik.core.it.check

import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL

internal class ItPortCheckerTest {

    @Test
    fun `duplicate connection`() {
        val symbolTable = ItSymbolTable()
        symbolTable.addComponent(ItModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = ItComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                null,
                listOf(
                        ItConnection(0, Symbol(1, 1, 3), ItUtil.EXPRESSION_NULL),
                        ItConnection(0, Symbol(1, 1, 3), ItUtil.EXPRESSION_NULL)
                )
        )
        assertThrowsMessage<LineException>("duplicate connection [[1, 1, 3]]") {
            ItPortChecker.checkComponentInstance(componentInstance, symbolTable)
        }
    }

    @Test
    fun `invalid connections`() {
        val symbolTable = ItSymbolTable()
        symbolTable.addComponent(ItModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = ItComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                null,
                listOf(
                        ItConnection(0, Symbol(1, 1, 3), ItUtil.EXPRESSION_NULL),
                        ItConnection(0, Symbol(1, 1, 4), ItUtil.EXPRESSION_NULL)
                )
        )
        assertThrowsMessage<LineException>("invalid connections [[1, 1, 3]], [[1, 1, 4]]") {
            ItPortChecker.checkComponentInstance(componentInstance, symbolTable)
        }
    }

    @Test
    fun `missing connections`() {
        val symbolTable = ItSymbolTable()
        symbolTable.addComponent(ItModule(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(
                        ItPort(0, "a", Symbol(1, 1, 2), TYPE_BOOL, null, ItPortType.INPUT, ItUtil.EXPRESSION_NULL),
                        ItPort(0, "b", Symbol(1, 1, 3), TYPE_BOOL, null, ItPortType.INPUT, ItUtil.EXPRESSION_NULL)
                ),
                listOf(),
                listOf(),
                listOf()
        ))
        val componentInstance = ItComponentInstance(
                0,
                "m",
                Symbol(1, 1, 4),
                Symbol(1, 1, 1),
                null,
                listOf()
        )
        assertThrowsMessage<LineException>("missing connections [[1, 1, 2]], [[1, 1, 3]]") {
            ItPortChecker.checkComponentInstance(componentInstance, symbolTable)
        }
    }
}