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

package verik.core.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.base.LiteralValue
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_IF
import verik.core.lang.LangSymbol.OPERATOR_IF_ELSE
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtExpressionParserPrimaryTest {

    @Test
    fun `parenthesized expression`() {
        val rule = AlRuleParser.parseExpression("(x)")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionProperty(1, null, "x", null, null), expression)
    }

    @Test
    fun `if expression`() {
        val rule = AlRuleParser.parseExpression("if (x) y")
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                1,
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionProperty(1, null, "x", null, null)),
                listOf(KtBlock(1, listOf(KtStatementExpression(
                        1,
                        KtExpressionProperty(1, null, "y", null, null)
                ))))
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
                OPERATOR_IF_ELSE,
                null,
                listOf(KtExpressionProperty(1, null, "x", null, null)),
                listOf(
                        KtBlock(1, listOf(KtStatementExpression(
                                1,
                                KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0))
                        ))),
                        KtBlock(1, listOf(KtStatementExpression(
                                1,
                                KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(1))
                        )))
                )
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `when expression`() {
        val rule = AlRuleParser.parseExpression("""
            when {
                0 -> {}
            }
        """.trimIndent())
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                2,
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionLiteral(2, TYPE_INT, LiteralValue.fromIntImplicit(0))),
                listOf(KtBlock(2, listOf()))
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `when else expression`() {
        val rule = AlRuleParser.parseExpression("""
            when {
                0 -> {}
                else -> {}
            }
        """.trimIndent())
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                2,
                null,
                OPERATOR_IF_ELSE,
                null,
                listOf(KtExpressionLiteral(2, TYPE_INT, LiteralValue.fromIntImplicit(0))),
                listOf(
                        KtBlock(2, listOf()),
                        KtBlock(3, listOf())
                )
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `when condition expression`() {
        val rule = AlRuleParser.parseExpression("""
            when (x) {
                0 -> {}
            }
        """.trimIndent())
        val expression = KtExpression(rule)
        val expected = KtExpressionOperator(
                2,
                null,
                OPERATOR_IF,
                null,
                listOf(KtExpressionFunction(
                        2,
                        null,
                        "eq",
                        KtExpressionProperty(1, null, "x", null, null),
                        listOf(KtExpressionLiteral(2, TYPE_INT, LiteralValue.fromIntImplicit(0))),
                        null
                )),
                listOf(KtBlock(2, listOf()))
        )
        assertEquals(expected, expression)
    }
}