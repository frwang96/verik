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

import io.verik.core.LinePos
import io.verik.core.sv.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkStatementExtractorTest {

    @Test
    fun `forever loop`() {
        val expression = VkCallableExpression(LinePos.ZERO, VkIdentifierExpression(LinePos.ZERO, "forever"),
                listOf(VkLambdaExpression(LinePos.ZERO, listOf())))
        val expected = SvLoopStatement(LinePos.ZERO, "forever", listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if statement`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.IF, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "0"), LinePos.ZERO)))
        ))
        val expected = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))), listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if else statement`() {
        val expression = VkOperatorExpression(LinePos.ZERO, VkOperatorType.IF_ELSE, listOf(
                VkIdentifierExpression(LinePos.ZERO, "x"),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "0"), LinePos.ZERO))),
                VkLambdaExpression(LinePos.ZERO,
                        listOf(VkStatement(VkLiteralExpression(LinePos.ZERO, "1"), LinePos.ZERO)))
        ))
        val expected = SvConditionalStatement(LinePos.ZERO, SvIdentifierExpression(LinePos.ZERO, "x"),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "0"))),
                listOf(SvExpressionStatement(LinePos.ZERO, SvLiteralExpression(LinePos.ZERO, "1"))))
        assertEquals(expected, expression.extractStatement())
    }
}