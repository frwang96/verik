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

package verik.core.rf.symbol

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.base.ast.TypeClass.TYPE
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verik.core.lang.LangSymbol.FUNCTION_TYPE_SINT
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.rf.ast.RfBlock
import verik.core.rf.ast.RfExpressionFunction
import verik.core.rf.ast.RfExpressionLiteral
import verik.core.rf.ast.RfExpressionOperator
import verik.core.sv.ast.*

internal class RfSymbolTableTest {

    @Test
    fun `reify operator forever`() {
        val operator = RfExpressionOperator(
                0,
                TYPE_UNIT,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(RfBlock(0, listOf()))
        )
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
                TYPE_REIFIED_UNIT,
                symbolTable.reifyOperator(operator)
        )
    }

    @Test
    fun `reify bool function`() {
        val expression = RfExpressionFunction(
                0,
                TYPE_BOOL,
                null,
                FUNCTION_TYPE_BOOL,
                null,
                listOf()
        )
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
                ReifiedType(TYPE_BOOL, TYPE, listOf()),
                symbolTable.reifyFunction(expression)
        )
    }

    @Test
    fun `reify sint function`() {
        val expression = RfExpressionFunction(
                0,
                TYPE_SINT,
                null,
                FUNCTION_TYPE_SINT,
                null,
                listOf(RfExpressionLiteral(
                        0,
                        TYPE_INT,
                        ReifiedType(TYPE_INT, INSTANCE, listOf()),
                        LiteralValue.fromInt(8)
                ))
        )
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
                ReifiedType(TYPE_SINT, TYPE, listOf(8)),
                symbolTable.reifyFunction(expression)
        )
    }

    @Test
    fun `extract uint`() {
        val reifiedType = ReifiedType(TYPE_UINT, INSTANCE, listOf(8))
        val symbolTable = RfSymbolTable()
        val expected = SvExtractedType("logic", "[7:0]", "")
        Assertions.assertEquals(
                expected,
                symbolTable.extractType(reifiedType, 0)
        )
    }

    @Test
    fun `reify sint function type class mismatch`() {
        val expression = RfExpressionFunction(
                0,
                TYPE_SINT,
                null,
                FUNCTION_TYPE_SINT,
                null,
                listOf(RfExpressionLiteral(
                        0,
                        TYPE_INT,
                        ReifiedType(TYPE_INT, TYPE, listOf()),
                        LiteralValue.fromInt(8)
                ))
        )
        val symbolTable = RfSymbolTable()
        assertThrowsMessage<LineException>("type class mismatch when resolving function $FUNCTION_TYPE_SINT") {
            symbolTable.reifyFunction(expression)
        }
    }

    @Test
    fun `extract uint illegal type class`() {
        val reifiedType = ReifiedType(TYPE_UINT, TYPE, listOf(8))
        val symbolTable = RfSymbolTable()
        assertThrowsMessage<LineException>("unable to extract type $TYPE_UINT(8) invalid type class") {
            symbolTable.extractType(reifiedType, 0)
        }
    }

    @Test
    fun `extract function finish`() {
        val expression = RfExpressionFunction(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                FUNCTION_FINISH,
                null,
                listOf()
        )
        val request = RfFunctionExtractorRequest(expression, null, listOf())
        val expected = SvStatementExpression.wrapFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractFunction(request)
        )
    }

    @Test
    fun `extract operator forever`() {
        val operator = RfExpressionOperator(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(RfBlock(0, listOf()))
        )
        val request = RfOperatorExtractorRequest(
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
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractOperator(request)
        )
    }
}