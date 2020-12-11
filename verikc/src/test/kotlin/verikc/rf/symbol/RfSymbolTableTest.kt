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

package verikc.rf.symbol

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.ReifiedType
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeClass.TYPE
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rf.ast.RfBlock
import verikc.rf.ast.RfExpressionFunction
import verikc.rf.ast.RfExpressionLiteral
import verikc.rf.ast.RfExpressionOperator

internal class RfSymbolTableTest {

    @Test
    fun `reify operator forever`() {
        val operator = RfExpressionOperator(
            Line(0),
            TYPE_UNIT,
            null,
            OPERATOR_FOREVER,
            null,
            listOf(),
            listOf(RfBlock(Line(0), listOf()))
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
            Line(0),
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
    fun `reify sbit function`() {
        val expression = RfExpressionFunction(
            Line(0),
            TYPE_SBIT,
            null,
            FUNCTION_TYPE_SBIT,
            null,
            listOf(
                RfExpressionLiteral(
                    Line(0),
                    TYPE_INT,
                    ReifiedType(TYPE_INT, INSTANCE, listOf()),
                    LiteralValue.fromInt(8)
                )
            )
        )
        val symbolTable = RfSymbolTable()
        Assertions.assertEquals(
            ReifiedType(TYPE_SBIT, TYPE, listOf(8)),
            symbolTable.reifyFunction(expression)
        )
    }

    @Test
    fun `reify sbit function type class mismatch`() {
        val expression = RfExpressionFunction(
            Line(0),
            TYPE_SBIT,
            null,
            FUNCTION_TYPE_SBIT,
            null,
            listOf(
                RfExpressionLiteral(
                    Line(0),
                    TYPE_INT,
                    ReifiedType(TYPE_INT, TYPE, listOf()),
                    LiteralValue.fromInt(8)
                )
            )
        )
        val symbolTable = RfSymbolTable()
        assertThrowsMessage<LineException>("type class mismatch when resolving function $FUNCTION_TYPE_SBIT") {
            symbolTable.reifyFunction(expression)
        }
    }
}
