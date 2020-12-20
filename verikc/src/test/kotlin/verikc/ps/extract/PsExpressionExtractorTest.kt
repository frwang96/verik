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

package verikc.ps.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.ps.PsUtil
import verikc.ps.ast.PsExpressionProperty
import verikc.ps.ast.PsPort
import verikc.ps.symbol.PsSymbolTable
import verikc.sv.ast.*

internal class PsExpressionExtractorTest {

    @Test
    fun `function finish`() {
        val string = "finish()"
        val expected = SvExpressionFunction(
            Line(1),
            null,
            "\$finish",
            listOf()
        )
        assertEquals(expected, PsUtil.extractExpression(string))
    }

    @Test
    fun `operator forever`() {
        val string = "forever {}"
        val expected = SvExpressionControlBlock(
            Line(1),
            SvControlBlockType.FOREVER,
            listOf(),
            listOf(SvBlock(Line(1), listOf()))
        )
        assertEquals(expected, PsUtil.extractExpression(string))
    }

    @Test
    fun `property bool`() {
        val expression = PsExpressionProperty(
            Line(0),
            TypeReified(TYPE_BOOL, INSTANCE, listOf()),
            Symbol(3),
            null
        )
        val symbolTable = PsSymbolTable()
        symbolTable.addProperty(
            PsPort(
                Line(0),
                "x",
                Symbol(3),
                TypeReified(TYPE_BOOL, INSTANCE, listOf()),
                PortType.INPUT
            )
        )
        val expected = SvExpressionProperty(Line(0), null, "x")
        assertEquals(expected, expression.extract(symbolTable))
    }
}
