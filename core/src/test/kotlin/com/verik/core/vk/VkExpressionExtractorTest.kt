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

package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvLiteralExpression
import com.verik.core.sv.SvOperatorExpression
import com.verik.core.sv.SvOperatorType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkExpressionExtractorTest {

    @Test
    fun `bocking assignment`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.PUT, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val expected = SvOperatorExpression(LinePos.ZERO, SvOperatorType.BASSIGN, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `arithmetic add`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.ADD, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkIdentifierExpression(LinePos.ZERO, "y")
        ))
        val expected = SvOperatorExpression(LinePos.ZERO, SvOperatorType.ADD, listOf(
                SvIdentifierExpression(LinePos.ZERO, "x"),
                SvIdentifierExpression(LinePos.ZERO, "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `literal zero`() {
        val expression = VkLiteralExpression(LinePos.ZERO, "0")
        val expected = SvLiteralExpression(LinePos.ZERO, "0")
        assertEquals(expected, expression.extractExpression())
    }
}