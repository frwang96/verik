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

package verik.core.ps.symbol

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.ast.LineException
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.TypeClass
import verik.core.lang.LangSymbol
import verik.core.ps.ast.PsBlock
import verik.core.ps.ast.PsExpressionFunction
import verik.core.ps.ast.PsExpressionOperator
import verik.core.sv.ast.SvBlock
import verik.core.sv.ast.SvControlBlockType
import verik.core.sv.ast.SvExtractedType
import verik.core.sv.ast.SvStatementExpression

internal class PsSymbolTableTest {

    @Test
    fun `extract uint`() {
        val reifiedType = ReifiedType(LangSymbol.TYPE_UINT, TypeClass.INSTANCE, listOf(8))
        val symbolTable = PsSymbolTable()
        val expected = SvExtractedType("logic", "[7:0]", "")
        Assertions.assertEquals(
                expected,
                symbolTable.extractType(reifiedType, 0)
        )
    }

    @Test
    fun `extract uint illegal type class`() {
        val reifiedType = ReifiedType(LangSymbol.TYPE_UINT, TypeClass.TYPE, listOf(8))
        val symbolTable = PsSymbolTable()
        assertThrowsMessage<LineException>("unable to extract type ${LangSymbol.TYPE_UINT}(8) invalid type class") {
            symbolTable.extractType(reifiedType, 0)
        }
    }

    @Test
    fun `extract function finish`() {
        val expression = PsExpressionFunction(
                0,
                LangSymbol.TYPE_REIFIED_UNIT,
                LangSymbol.FUNCTION_FINISH,
                null,
                arrayListOf()
        )
        val request = PsFunctionExtractorRequest(expression, null, listOf())
        val expected = SvStatementExpression.wrapFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        val symbolTable = PsSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractFunction(request)
        )
    }

    @Test
    fun `extract operator forever`() {
        val operator = PsExpressionOperator(
                0,
                LangSymbol.TYPE_REIFIED_UNIT,
                LangSymbol.OPERATOR_FOREVER,
                null,
                arrayListOf(),
                listOf(PsBlock(0, arrayListOf()))
        )
        val request = PsOperatorExtractorRequest(
                operator,
                null,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val expected = SvStatementExpression.wrapControlBlock(
                0,
                SvControlBlockType.FOREVER,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val symbolTable = PsSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractOperator(request)
        )
    }
}