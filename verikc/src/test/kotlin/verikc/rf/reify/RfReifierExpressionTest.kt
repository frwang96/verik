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

package verikc.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.PortType
import verikc.base.ast.ReifiedType
import verikc.base.ast.Symbol
import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rf.RfUtil
import verikc.rf.ast.*
import verikc.rf.symbol.RfSymbolTable

internal class RfReifierExpressionTest {

    @Test
    fun `function finish`() {
        val expression = RfExpressionFunction(
            Line(0),
            TYPE_UNIT,
            null,
            FUNCTION_FINISH,
            null,
            listOf()
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.reifiedType)
    }

    @Test
    fun `operator forever`() {
        val expression = RfExpressionOperator(
            Line(0),
            TYPE_UNIT,
            null,
            OPERATOR_FOREVER,
            null,
            listOf(),
            listOf(RfBlock(Line(0), listOf()))
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.reifiedType)
    }

    @Test
    fun `property bool`() {
        val expression = RfExpressionProperty(
            Line(0),
            TYPE_BOOL,
            null,
            Symbol(1, 1, 1),
            null
        )
        val symbolTable = RfSymbolTable()
        symbolTable.addProperty(
            RfPort(
                Line(0),
                "x",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                PortType.INPUT,
                RfUtil.EXPRESSION_NULL
            )
        )
        RfReifierExpression.reify(expression, symbolTable)
        assertEquals(
            ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
            expression.reifiedType
        )
    }

    @Test
    fun `string literal`() {
        val expression = RfExpressionString(
            Line(0),
            TYPE_STRING,
            null,
            listOf()
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(
            ReifiedType(TYPE_STRING, INSTANCE, listOf()),
            expression.reifiedType
        )
    }
}
