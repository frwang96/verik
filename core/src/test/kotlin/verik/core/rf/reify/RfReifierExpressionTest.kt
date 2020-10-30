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

package verik.core.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.ast.Symbol
import verik.core.rf.*
import verik.core.rf.symbol.RfSymbolTable
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_STRING
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.rf.ast.*

internal class RfReifierExpressionTest {

    @Test
    fun `function finish`() {
        val expression = RfExpressionFunction(
                0,
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
                0,
                TYPE_UNIT,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(RfBlock(0, listOf()))
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.reifiedType)
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
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                RfPortType.INPUT,
                RfUtil.EXPRESSION_NULL
        ))
        RfReifierExpression.reify(expression, symbolTable)
        assertEquals(
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                expression.reifiedType
        )
    }

    @Test
    fun `string literal`() {
        val expression = RfExpressionString(
                0,
                TYPE_STRING,
                null,
                listOf()
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(
                RfReifiedType(TYPE_STRING, RfTypeClass.INSTANCE, listOf()),
                expression.reifiedType
        )
    }
}