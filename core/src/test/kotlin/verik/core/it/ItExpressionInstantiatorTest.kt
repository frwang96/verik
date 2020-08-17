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
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.main.symbol.Symbol
import verik.core.vk.VkExpressionFunction
import verik.core.vk.VkExpressionLiteral
import verik.core.vk.VkExpressionProperty

internal class ItExpressionInstantiatorTest {

    @Test
    fun `function bool`() {
        val expression = VkExpressionFunction(
                0,
                TYPE_BOOL,
                FUNCTION_BOOL,
                null,
                listOf()
        )
        val expected = ItExpressionFunction(
                0,
                TYPE_BOOL,
                null,
                FUNCTION_BOOL,
                null,
                listOf()
        )
        assertEquals(expected, ItExpressionInstantiator.instantiate(expression))
    }

    @Test
    fun `property bool`() {
        val expression = VkExpressionProperty(
                0,
                TYPE_BOOL,
                Symbol(1, 1, 1),
                null
        )
        val expected = ItExpressionProperty(
                0,
                TYPE_BOOL,
                null,
                Symbol(1, 1, 1),
                null
        )
        assertEquals(expected, ItExpressionInstantiator.instantiate(expression))
    }

    @Test
    fun `literal integer`() {
        val expression = VkExpressionLiteral(0, TYPE_INT, "0")
        val expected = ItExpressionLiteral(
                0,
                TYPE_INT,
                null,
                "0"
        )
        assertEquals(expected, ItExpressionInstantiator.instantiate(expression))
    }
}