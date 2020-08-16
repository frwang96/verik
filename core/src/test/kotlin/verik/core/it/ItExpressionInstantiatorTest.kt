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

package verik.core.it

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertStringEquals
import verik.core.lang.LangSymbol.FUNCTION_BOOL_TYPE
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.vk.VkExpressionFunction
import verik.core.vk.VkExpressionLiteral

internal class ItExpressionInstantiatorTest {

    @Test
    fun `bool type function`() {
        val expression = VkExpressionFunction(0, TYPE_BOOL, null, listOf(), FUNCTION_BOOL_TYPE)
        val expected = ItExpressionFunction(
                0,
                ItTypeInstance(TYPE_BOOL, ItTypeClass.TYPE, listOf()),
                null,
                listOf(),
                FUNCTION_BOOL_TYPE
        )
        assertStringEquals(expected, ItExpressionInstantiator.instantiate(expression))
    }

    @Test
    fun `integer literal`() {
        val expression = VkExpressionLiteral(0, TYPE_INT, "0")
        val expected = ItExpressionLiteral(
                0,
                ItTypeInstance(TYPE_INT, ItTypeClass.INT, listOf()),
                "0"
        )
        assertEquals(expected, ItExpressionInstantiator.instantiate(expression))
    }
}