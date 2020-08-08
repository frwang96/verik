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

package io.verik.core.kt

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assert.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtPrimaryExpressionParserTest {

    @Test
    fun `parenthesized expression`() {
        val rule = AlRuleParser.parseExpression("(x)")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionIdentifier(FileLine(1), null, "x"), expression)
    }

    @Test
    fun `literal constant bool`() {
        val rule = AlRuleParser.parseExpression("false")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionLiteral(FileLine(1), "0"), expression)
    }

    @Test
    fun `literal constant int`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = KtExpression(rule)
        assertEquals(KtExpressionLiteral(FileLine(1), "0"), expression)
    }

    @Test
    fun `string simple`() {
        val rule = AlRuleParser.parseExpression("\"x\"")
        val expression = KtExpression(rule)
        val expected = KtExpressionString(FileLine(1), listOf(
                KtStringSegmentLiteral("x")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `string reference`() {
        val rule = AlRuleParser.parseExpression("\"\$x\"")
        val expression = KtExpression(rule)
        val expected = KtExpressionString(FileLine(1), listOf(
                KtStringSegmentExpression(KtExpressionIdentifier(FileLine(1), null, "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `string expression`() {
        val rule = AlRuleParser.parseExpression("\"\${x}\"")
        val expression = KtExpression(rule)
        val expected = KtExpressionString(FileLine(1), listOf(
                KtStringSegmentExpression(KtExpressionIdentifier(FileLine(1), null, "x"))
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `string escape sequence`() {
        val rule = AlRuleParser.parseExpression("\"\\n\"")
        val expression = KtExpression(rule)
        val expected = KtExpressionString(FileLine(1), listOf(
                KtStringSegmentLiteral("\\n")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `string escape sequence converted`() {
        val rule = AlRuleParser.parseExpression("\"\\'\"")
        val expression = KtExpression(rule)
        val expected = KtExpressionString(FileLine(1), listOf(
                KtStringSegmentLiteral("'")
        ))
        assertEquals(expected, expression)
    }

    @Test
    fun `string escape sequence illegal`() {
        val rule = AlRuleParser.parseExpression("\"\\u0000\"")
        assertThrowsMessage<FileLineException>("illegal escape sequence \\u0000") {
            KtExpression(rule)
        }
    }

    @Test
    fun `if expression`() {
        val rule = AlRuleParser.parseExpression("if (x) y")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                FileLine(1),
                KtExpressionIdentifier(FileLine(1), null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.IF),
                listOf(KtExpressionLambda(FileLine(1),
                        KtBlock(listOf(
                                KtStatement(KtExpressionIdentifier(FileLine(1), null, "y"), FileLine(1))
                        ), FileLine(1))
                ))
        )
        assertEquals(expected, expression)
    }

    @Test
    fun `if else expression`() {
        val rule = AlRuleParser.parseExpression("if (x) 0 else 1")
        val expression = KtExpression(rule)
        val expected = KtExpressionFunction(
                FileLine(1),
                KtExpressionIdentifier(FileLine(1), null, "x"),
                KtFunctionIdentifierOperator(KtOperatorType.IF_ELSE),
                listOf(
                        KtExpressionLambda(FileLine(1),
                                KtBlock(listOf(
                                        KtStatement(KtExpressionLiteral(FileLine(1), "0"), FileLine(1))
                                ), FileLine(1))
                        ),
                        KtExpressionLambda(FileLine(1),
                                KtBlock(listOf(
                                        KtStatement(KtExpressionLiteral(FileLine(1), "1"), FileLine(1))
                                ), FileLine(1))
                        )
                )
        )
        assertEquals(expected, expression)
    }
}