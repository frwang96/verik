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
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeInstance
import verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import verik.core.lang.LangSymbol.FUN_FINISH
import verik.core.lang.LangSymbol.FUN_SINT_TYPE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.SvExpressionFunction
import verik.core.vk.VkExpressionFunction
import verik.core.vk.VkExpressionLiteral

internal class LangFunctionTableTest {

    @Test
    fun `no match`() {
        assertEquals(
                LangFunctionMatchNone,
                Lang.functionTable.match("none", listOf())
        )
    }

    @Test
    fun `match bool type function`() {
        assertEquals(
                LangFunctionMatchSingle(FUN_BOOL_TYPE, TYPE_BOOL),
                Lang.functionTable.match("_bool", listOf())
        )
    }

    @Test
    fun `instantiate bool type function`() {
        val expression = Lang.functionTable.instantiate(LangFunctionInstantiatorRequest(
                VkExpressionFunction(0, TYPE_BOOL, null, listOf(), FUN_BOOL_TYPE),
                null,
                listOf()
        ))
        val expected = ItExpressionFunction(
                0,
                ItTypeInstance(TYPE_BOOL, listOf()),
                null,
                listOf(),
                FUN_BOOL_TYPE
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `instantiate sint type function`() {
        val expression = VkExpressionFunction(
                0,
                TYPE_SINT,
                null,
                listOf(VkExpressionLiteral(0, TYPE_INT, "8")),
                FUN_SINT_TYPE
        )
        val request = LangFunctionInstantiatorRequest(
                expression,
                null,
                listOf(ItExpressionLiteral(0, ItTypeInstance(TYPE_INT, listOf()), "8"))
        )
        val expected = ItExpressionFunction(
                0,
                ItTypeInstance(TYPE_SINT, listOf(8)),
                null,
                listOf(ItExpressionLiteral(0, ItTypeInstance(TYPE_INT, listOf()), "8")),
                FUN_SINT_TYPE
        )
        assertEquals(expected, Lang.functionTable.instantiate(request))
    }

    @Test
    fun `extract function finish`() {
        val expression = ItExpressionFunction(
                0,
                ItTypeInstance(TYPE_UNIT, listOf()),
                null,
                listOf(),
                FUN_FINISH
        )
        val request = LangFunctionExtractorRequest(
                expression,
                null,
                listOf()
        )
        val expected = SvExpressionFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, Lang.functionTable.extract(request))
    }
}