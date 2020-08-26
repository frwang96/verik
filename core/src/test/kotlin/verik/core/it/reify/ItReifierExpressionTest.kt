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

package verik.core.it.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.Symbol
import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_STRING
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class ItReifierExpressionTest {

    @Test
    fun `function finish`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_UNIT,
                null,
                FUNCTION_FINISH,
                null,
                listOf()
        )
        ItReifierExpression.reify(expression, ItSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.reifiedType)
    }

    @Test
    fun `operator forever`() {
        val expression = ItExpressionOperator(
                0,
                TYPE_UNIT,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        ItReifierExpression.reify(expression, ItSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.reifiedType)
    }

    @Test
    fun `property bool`() {
        val expression = ItExpressionProperty(
                0,
                TYPE_BOOL,
                null,
                Symbol(1, 1, 1),
                null
        )
        val symbolTable = ItSymbolTable()
        symbolTable.addProperty(ItPort(
                0,
                "x",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                ItReifiedType(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                ItPortType.INPUT,
                ItUtil.EXPRESSION_NULL
        ))
        ItReifierExpression.reify(expression, symbolTable)
        assertEquals(
                ItReifiedType(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                expression.reifiedType
        )
    }

    @Test
    fun `string literal`() {
        val expression = ItExpressionString(
                0,
                TYPE_STRING,
                null,
                listOf()
        )
        ItReifierExpression.reify(expression, ItSymbolTable())
        assertEquals(
                ItReifiedType(TYPE_STRING, ItTypeClass.INSTANCE, listOf()),
                expression.reifiedType
        )
    }
}