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

package io.verik.core.vk

import io.verik.core.main.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkInstanceTypeTest {

    @Test
    fun `bool type`() {
        val expression = VkExpression(AlRuleParser.parseExpression("_bool()")) as VkExpressionCallable
        val type = VkInstanceType(expression)
        assertEquals(VkBoolType, type)
    }

    @Test
    fun `bool type illegal parameter`() {
        val expression = VkExpression(AlRuleParser.parseExpression("_bool(1)")) as VkExpressionCallable
        assertThrowsMessage<LineException>("type _bool does not take parameters") {
            VkInstanceType(expression)
        }
    }

    @Test
    fun `sint type`() {
        val expression = VkExpression(AlRuleParser.parseExpression("_sint(1)")) as VkExpressionCallable
        val type = VkInstanceType(expression)
        assertEquals(VkSintType(1), type)
    }

    @Test
    fun `uint type`() {
        val expression = VkExpression(AlRuleParser.parseExpression("_uint(1)")) as VkExpressionCallable
        val type = VkInstanceType(expression)
        assertEquals(VkUintType(1), type)
    }

    @Test
    fun `uint type no parameter`() {
        val expression = VkExpression(AlRuleParser.parseExpression("_uint()")) as VkExpressionCallable
        assertThrowsMessage<LineException>("type _uint takes one parameter") {
            VkInstanceType(expression)
        }
    }
}