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

package io.verik.core.lang

import io.verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import io.verik.core.lang.LangSymbol.FUN_FINISH
import io.verik.core.lang.LangSymbol.FUN_UINT_TYPE
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.lang.LangSymbol.TYPE_UINT
import io.verik.core.svx.SvxExpressionFunction
import io.verik.core.vkx.VkxExpressionFunction
import io.verik.core.vkx.VkxExpressionLiteral
import io.verik.core.vkx.VkxType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
    fun `resolve uint type function`() {
        val args = listOf(VkxExpressionLiteral(0, TYPE_INT, null, "1"))
        assertEquals(
                VkxType(TYPE_UINT, listOf(1)),
                Lang.functionTable.resolve(FUN_UINT_TYPE, args)
        )
    }

    @Test
    fun `extract finish function`() {
        val expression = VkxExpressionFunction(
                0,
                TYPE_UINT,
                VkxType(TYPE_UINT, listOf()),
                null,
                listOf(),
                FUN_FINISH
        )
        val expected = SvxExpressionFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, Lang.functionTable.extract(expression, listOf()))
    }
}