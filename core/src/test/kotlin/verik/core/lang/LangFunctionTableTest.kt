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
import verik.core.lang.LangSymbol.FUNCTION_BOOL_TYPE
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_SINT_TYPE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.main.LineException
import verik.core.sv.SvExpressionFunction
import verik.core.vk.VkExpressionFunction
import verik.core.vk.VkExpressionLiteral

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
    fun `resolve bool type function`() {
        val function = KtExpressionFunction(
                0,
                null,
                "_bool",
                null,
                listOf(),
                null
        )
        assertEquals(
                FUNCTION_BOOL_TYPE,
                Lang.functionTable.resolve(function, listOf()).symbol
        )
    }

    @Test
    fun `instantiate bool type function`() {
        val expression = Lang.functionTable.instantiate(LangFunctionReifierRequest(
                VkExpressionFunction(0, TYPE_BOOL, FUNCTION_BOOL_TYPE, null, listOf()),
                null,
                listOf()
        ))
        val expected = ItExpressionFunction(
                0,
                TYPE_BOOL,
                ItTypeReified(TYPE_BOOL, ItTypeClass.TYPE, listOf()),
                FUNCTION_BOOL_TYPE,
                null,
                listOf()
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `instantiate sint type function`() {
        val expression = VkExpressionFunction(
                0,
                TYPE_SINT,
                FUNCTION_SINT_TYPE,
                null,
                listOf(VkExpressionLiteral(0, TYPE_INT, "8"))
        )
        val args = listOf(ItExpressionLiteral(
                0,
                TYPE_INT,
                ItTypeReified(TYPE_INT, ItTypeClass.INT, listOf()),
                "8"
        ))
        val request = LangFunctionReifierRequest(expression, null, args)
        val expected = ItExpressionFunction(
                0,
                TYPE_SINT,
                ItTypeReified(TYPE_SINT, ItTypeClass.TYPE, listOf(8)),
                FUNCTION_SINT_TYPE,
                null,
                args
        )
        assertEquals(expected, Lang.functionTable.instantiate(request))
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