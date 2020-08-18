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

package verik.core.lang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.kt.KtExpressionFunction
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_SINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.main.LineException
import verik.core.sv.SvExpressionFunction

internal class LangFunctionTableTest {

    @Test
    fun `resolve none`() {
        val function = KtExpressionFunction(
                0,
                null,
                "none",
                null,
                listOf(),
                null
        )
        assertThrowsMessage<LineException>("function none could not be resolved") {
            Lang.functionTable.resolve(function, listOf())
        }
    }

    @Test
    fun `resolve bool function`() {
        val function = KtExpressionFunction(
                0,
                null,
                "_bool",
                null,
                listOf(),
                null
        )
        assertEquals(
                FUNCTION_BOOL,
                Lang.functionTable.resolve(function, listOf()).symbol
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
        Lang.functionTable.reify(expression)
        assertEquals(
                ItTypeReified(TYPE_BOOL, ItTypeClass.TYPE, listOf()),
                expression.typeReified
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
                listOf(ItExpressionLiteral(0, TYPE_INT, TYPE_REIFIED_INT, false, 4, 8))
        )
        Lang.functionTable.reify(expression)
        assertEquals(
                ItTypeReified(TYPE_SINT, ItTypeClass.TYPE, listOf(8)),
                expression.typeReified
        )
    }

    @Test
    fun `extract function finish`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_UNIT,
                ItTypeReified(TYPE_UNIT, ItTypeClass.UNIT, listOf()),
                FUNCTION_FINISH,
                null,
                listOf()
        )
        val request = LangFunctionExtractorRequest(expression, null, listOf())
        val expected = SvExpressionFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, Lang.functionTable.extract(request))
    }
}