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

import io.verik.core.FileLineException
import io.verik.core.assert.assertThrowsMessage
import io.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkDataTypeTest {

    @Test
    fun `bool type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_bool()")) as VkCallableExpression
        val type = VkDataType(expression)
        assertEquals(VkBoolType, type)
    }

    @Test
    fun `bool type illegal parameter`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_bool(1)")) as VkCallableExpression
        assertThrowsMessage<FileLineException>("type _bool does not take parameters") {
            VkDataType(expression)
        }
    }

    @Test
    fun `sint type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_sint(1)")) as VkCallableExpression
        val type = VkDataType(expression)
        assertEquals(VkSintType(1), type)
    }

    @Test
    fun `uint type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_uint(1)")) as VkCallableExpression
        val type = VkDataType(expression)
        assertEquals(VkUintType(1), type)
    }

    @Test
    fun `uint type no parameter`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_uint()")) as VkCallableExpression
        assertThrowsMessage<FileLineException>("type _uint takes one parameter") {
            VkDataType(expression)
        }
    }
}