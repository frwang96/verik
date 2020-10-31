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

package verik.core.ps.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.ast.PortType
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.ps.PsUtil
import verik.core.ps.ast.PsExpressionProperty
import verik.core.ps.ast.PsPort
import verik.core.ps.symbol.PsSymbolTable
import verik.core.sv.ast.*

internal class PsExpressionExtractorTest {

    @Test
    fun `function finish`() {
        val string = "finish()"
        val expected = SvExpressionFunction(
                1,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, PsUtil.extractExpression(string))
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
        assertEquals(expected, PsUtil.extractStatement(string))
    }

    @Test
    fun `property bool`() {
        val expression = PsExpressionProperty(
                0,
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                Symbol(1, 1, 1),
                null
        )
        val symbolTable = PsSymbolTable()
        symbolTable.addProperty(PsPort(
                0,
                "x",
                Symbol(1, 1, 1),
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                PortType.INPUT
        ))
        val expected = SvStatementExpression.wrapProperty(0, null, "x")
        assertEquals(expected, expression.extract(symbolTable))
    }
}