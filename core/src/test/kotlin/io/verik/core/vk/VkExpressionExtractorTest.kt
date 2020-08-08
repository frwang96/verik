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
import io.verik.core.sv.SvExpressionIdentifier
import io.verik.core.sv.SvExpressionLiteral
import io.verik.core.sv.SvExpressionOperator
import io.verik.core.sv.SvOperatorType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkExpressionExtractorTest {

    @Test
    fun `bocking assignment`() {
        val expression = VkExpressionOperator(FileLine(), VkOperatorType.PUT, listOf(
                VkExpressionIdentifier(FileLine(), "x"),
                VkExpressionIdentifier(FileLine(), "y")
        ))
        val expected = SvExpressionOperator(FileLine(), SvOperatorType.BASSIGN, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionIdentifier(FileLine(), "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `arithmetic add`() {
        val expression = VkExpressionOperator(FileLine(), VkOperatorType.ADD, listOf(
                VkExpressionIdentifier(FileLine(), "x"),
                VkExpressionIdentifier(FileLine(), "y")
        ))
        val expected = SvExpressionOperator(FileLine(), SvOperatorType.ADD, listOf(
                SvExpressionIdentifier(FileLine(), "x"),
                SvExpressionIdentifier(FileLine(), "y")
        ))
        assertEquals(expected, expression.extractExpression())
    }

    @Test
    fun `literal zero`() {
        val expression = VkExpressionLiteral(FileLine(), "0")
        val expected = SvExpressionLiteral(FileLine(), "0")
        assertEquals(expected, expression.extractExpression())
    }
}