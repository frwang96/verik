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

package verik.core.kt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser

internal class KtExpressionParserPrimaryTest {

    @Test
    fun `parenthesized expression`() {
        val rule = AlRuleParser.parseExpression("(x)")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionProperty(1, null, "x", null, null), expression)
    }

    @Test
    fun `literal constant bool`() {
        val rule = AlRuleParser.parseExpression("false")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionLiteral(1, null, "false"), expression)
    }

    @Test
    fun `literal constant int`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionLiteral(1, null, "0"), expression)
    }

    @Test
    fun `if expression`() {
        val rule = AlRuleParser.parseExpression("if (x) y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.IF,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(KtBlock(1, listOf(KtStatement(1, KtExpressionProperty(1, null, "y", null, null)))))
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `if else expression`() {
        val rule = AlRuleParser.parseExpression("if (x) 0 else 1")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                KtOperatorIdentifier.IF_ELSE,
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                listOf(
                        KtBlock(1, listOf(KtStatement(1, KtExpressionLiteral(1, null, "0")))),
                        KtBlock(1, listOf(KtStatement(1, KtExpressionLiteral(1, null, "1"))))
                )
        )
        assertEquals(expected, expression)
    }
}