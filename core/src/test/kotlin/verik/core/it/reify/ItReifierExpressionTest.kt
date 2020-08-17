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
import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.main.symbol.Symbol

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
        ItReifierExpression.reifyExpression(expression, ItSymbolTable())
        assertEquals(TYPE_REIFIED_UNIT, expression.typeReified)
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
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                ItPortType.INPUT,
                ItExpressionLiteral(0, TYPE_UNIT, TYPE_REIFIED_UNIT, "")
        ))
        ItReifierExpression.reifyExpression(expression, symbolTable)
        assertEquals(
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                expression.typeReified
        )
    }

    @Test
    fun `literal bool`() {
        val expression = ItExpressionLiteral(
                0,
                TYPE_BOOL,
                null,
                "false"
        )
        ItReifierExpression.reifyExpression(expression, ItSymbolTable())
        assertEquals(
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                expression.typeReified
        )
    }
}