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

package verik.core.rf.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.ast.PortType
import verik.core.base.ast.Symbol
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.rf.RfUtil
import verik.core.rf.ast.RfExpressionProperty
import verik.core.rf.ast.RfPort
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.*

internal class RfExpressionExtractorTest {

    @Test
    fun `function finish`() {
        val string = "finish()"
        val expected = SvExpressionFunction(
                1,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, RfUtil.extractExpression(string))
    }

    @Test
    fun `operator forever`() {
        val string = "forever {}"
        val expected = SvStatementControlBlock(
                1,
                SvControlBlockType.FOREVER,
                listOf(),
                listOf(SvBlock(1, listOf()))
        )
        assertEquals(expected, RfUtil.extractStatement(string))
    }

    @Test
    fun `property bool`() {
        val expression = RfExpressionProperty(
                0,
                TYPE_BOOL,
                null,
                Symbol(1, 1, 1),
                null
        )
        val symbolTable = RfSymbolTable()
        symbolTable.addProperty(RfPort(
                0,
                "x",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                null,
                PortType.INPUT,
                RfUtil.EXPRESSION_NULL
        ))
        val expected = SvStatementExpression.wrapProperty(
                0,
                null,
                "x"
        )
        assertEquals(expected, expression.extract(symbolTable))
    }
}