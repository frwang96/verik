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

import io.verik.core.FileLine
import io.verik.core.sv.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkStatementExtractorTest {

    @Test
    fun `forever loop`() {
        val expression = VkCallableExpression(FileLine(), VkIdentifierExpression(FileLine(), "forever"),
                listOf(VkLambdaExpression(FileLine(), listOf())))
        val expected = SvLoopStatement(FileLine(), "forever", listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if statement`() {
        val expression = VkOperatorExpression(FileLine(), VkOperatorType.IF, listOf(
                VkIdentifierExpression(FileLine(), "x"),
                VkLambdaExpression(FileLine(),
                        listOf(VkStatement(VkLiteralExpression(FileLine(), "0"), FileLine())))
        ))
        val expected = SvConditionalStatement(FileLine(), SvIdentifierExpression(FileLine(), "x"),
                listOf(SvExpressionStatement(FileLine(), SvLiteralExpression(FileLine(), "0"))), listOf())
        assertEquals(expected, expression.extractStatement())
    }

    @Test
    fun `if else statement`() {
        val expression = VkOperatorExpression(FileLine(), VkOperatorType.IF_ELSE, listOf(
                VkIdentifierExpression(FileLine(), "x"),
                VkLambdaExpression(FileLine(),
                        listOf(VkStatement(VkLiteralExpression(FileLine(), "0"), FileLine()))),
                VkLambdaExpression(FileLine(),
                        listOf(VkStatement(VkLiteralExpression(FileLine(), "1"), FileLine())))
        ))
        val expected = SvConditionalStatement(FileLine(), SvIdentifierExpression(FileLine(), "x"),
                listOf(SvExpressionStatement(FileLine(), SvLiteralExpression(FileLine(), "0"))),
                listOf(SvExpressionStatement(FileLine(), SvLiteralExpression(FileLine(), "1"))))
        assertEquals(expected, expression.extractStatement())
    }
}