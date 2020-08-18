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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtExpressionParserLiteralTest {

    @Test
    fun `bool false`() {
        val rule = AlRuleParser.parseExpression("false")
        val expression = KtExpression(rule)
        Assertions.assertEquals(
                KtExpressionLiteral(1, TYPE_BOOL, true, 1, 0),
                expression
        )
    }

    @Test
    fun `int bin`() {
        val rule = AlRuleParser.parseExpression("0b0000_1111")
        val expression = KtExpression(rule)
        Assertions.assertEquals(
                KtExpressionLiteral(1, TYPE_INT, true, 8, 15),
                expression
        )
    }

    @Test
    fun `int hex`() {
        val rule = AlRuleParser.parseExpression("0X00ff")
        val expression = KtExpression(rule)
        Assertions.assertEquals(
                KtExpressionLiteral(1, TYPE_INT, true, 16, 255),
                expression
        )
    }

    @Test
    fun `int dec`() {
        val rule = AlRuleParser.parseExpression("3")
        val expression = KtExpression(rule)
        Assertions.assertEquals(
                KtExpressionLiteral(1, TYPE_INT, false, 2, 3),
                expression
        )
    }

    @Test
    fun `int dec zero`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = KtExpression(rule)
        Assertions.assertEquals(
                KtExpressionLiteral(1, TYPE_INT, false, 1, 0),
                expression
        )
    }
}