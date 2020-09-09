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

package verik.core.it.symbol

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.it.*
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_SINT
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.*

internal class ItSymbolTableTest {

    @Test
    fun `reify operator forever`() {
        val operator = ItExpressionOperator(
                0,
                TYPE_UNIT,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                TYPE_REIFIED_UNIT,
                symbolTable.reifyOperator(operator)
        )
    }

    @Test
    fun `reify bool function`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_BOOL,
                null,
                FUNCTION_BOOL,
                null,
                listOf()
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                ItReifiedType(TYPE_BOOL, ItTypeClass.TYPE, listOf()),
                symbolTable.reifyFunction(expression)
        )
    }

    @Test
    fun `reify sint function`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_SINT,
                null,
                FUNCTION_SINT,
                null,
                listOf(ItExpressionLiteral(
                        0,
                        TYPE_INT,
                        ItReifiedType(TYPE_INT, ItTypeClass.INSTANCE, listOf()),
                        LiteralValue.fromIntImplicit(8)
                ))
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                ItReifiedType(TYPE_SINT, ItTypeClass.TYPE, listOf(8)),
                symbolTable.reifyFunction(expression)
        )
    }

    @Test
    fun `extract uint`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8))
        val symbolTable = ItSymbolTable()
        val expected = SvReifiedType("logic", "[7:0]", "")
        Assertions.assertEquals(
                expected,
                symbolTable.extractType(reifiedType, 0)
        )
    }

    @Test
    fun `extract uint illegal type class`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.TYPE, listOf(8))
        val symbolTable = ItSymbolTable()
        assertThrowsMessage<LineException>("unable to extract type $TYPE_UINT(8) invalid type class") {
            symbolTable.extractType(reifiedType, 0)
        }
    }

    @Test
    fun `extract function finish`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                FUNCTION_FINISH,
                null,
                listOf()
        )
        val request = ItFunctionExtractorRequest(expression, null, listOf())
        val expected = SvStatementExpression.wrapFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractFunction(request)
        )
    }

    @Test
    fun `extract operator forever`() {
        val operator = ItExpressionOperator(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        val request = ItOperatorExtractorRequest(
                operator,
                null,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val expected = SvStatementControlBlock(
                0,
                SvControlBlockType.FOREVER,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractOperator(request)
        )
    }
}