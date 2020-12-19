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

package verikc.ps.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.*
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsBlock
import verikc.ps.ast.PsEnum
import verikc.ps.ast.PsExpressionFunction
import verikc.ps.ast.PsExpressionOperator
import verikc.sv.ast.*

internal class PsSymbolTableTest {

    @Test
    fun `extract ubit`() {
        val typeReified = TypeReified(TYPE_UBIT, TypeClass.INSTANCE, listOf(8))
        val symbolTable = PsSymbolTable()
        val expected = SvTypeExtracted("logic", "[7:0]", "")
        assertEquals(
            expected,
            symbolTable.extractType(typeReified, Line(0))
        )
    }

    @Test
    fun `extract ubit illegal type class`() {
        val typeReified = TypeReified(TYPE_UBIT, TypeClass.TYPE, listOf(8))
        val symbolTable = PsSymbolTable()
        assertThrowsMessage<LineException>("unable to extract type $TYPE_UBIT(8) invalid type class") {
            symbolTable.extractType(typeReified, Line(0))
        }
    }

    @Test
    fun `extract function finish`() {
        val expression = PsExpressionFunction(
            Line(0),
            LangSymbol.TYPE_REIFIED_UNIT,
            LangSymbol.FUNCTION_FINISH,
            null,
            arrayListOf()
        )
        val request = PsFunctionExtractorRequest(expression, null, listOf())
        val expected = SvExpressionFunction(
            Line(0),
            null,
            "\$finish",
            listOf()
        )
        val symbolTable = PsSymbolTable()
        assertEquals(
            expected,
            symbolTable.extractFunction(request)
        )
    }

    @Test
    fun `extract operator forever`() {
        val operator = PsExpressionOperator(
            Line(0),
            LangSymbol.TYPE_REIFIED_UNIT,
            LangSymbol.OPERATOR_FOREVER,
            null,
            arrayListOf(),
            listOf(PsBlock(Line(0), arrayListOf()))
        )
        val request = PsOperatorExtractorRequest(
            operator,
            null,
            listOf(),
            listOf(SvBlock(Line(0), listOf()))
        )
        val expected = SvExpressionControlBlock(
            Line(0),
            SvControlBlockType.FOREVER,
            listOf(),
            listOf(SvBlock(Line(0), listOf()))
        )
        val symbolTable = PsSymbolTable()
        assertEquals(
            expected,
            symbolTable.extractOperator(request)
        )
    }

    @Test
    fun `extract type enum`() {
        val enum = PsEnum(
            Line(0),
            "_op",
            Symbol(3),
            listOf(),
            0
        )
        val symbolTable = PsSymbolTable()
        symbolTable.addType(enum)
        assertEquals(
            SvTypeExtracted("_op", "", ""),
            symbolTable.extractType(TypeReified(Symbol(3), TypeClass.INSTANCE, listOf()), Line(0))
        )
    }
}
