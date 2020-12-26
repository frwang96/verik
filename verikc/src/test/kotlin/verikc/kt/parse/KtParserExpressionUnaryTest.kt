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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserExpressionUnaryTest {

    @Test
    fun `prefix unary expression`() {
        val expression = KtParseUtil.parseExpression("!x")
        val expected = KtExpressionFunction(
            line(3),
            null,
            "!",
            KtExpressionProperty(line(3), null, "x", null, null),
            listOf(),
            null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix expression`() {
        val expression = KtParseUtil.parseExpression("x[0]")
        val expected = KtExpressionFunction(
            line(3),
            null,
            "get",
            KtExpressionProperty(line(3), null, "x", null, null),
            listOf(KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0))),
            null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix expression`() {
        val expression = KtParseUtil.parseExpression("x.y")
        val expected = KtExpressionProperty(
            line(3),
            null,
            "y",
            KtExpressionProperty(line(3), null, "x", null, null),
            null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression`() {
        val expression = KtParseUtil.parseExpression("x()")
        val expected = KtExpressionFunction(
            line(3),
            null,
            "x",
            null,
            listOf(),
            null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression with lambda`() {
        val expression = KtParseUtil.parseExpression("on() {}")
        val expected = KtExpressionOperator(
            line(3),
            null,
            OPERATOR_ON,
            null,
            listOf(),
            listOf(KtBlock(line(3), Symbol(5), listOf(), listOf()))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression with lambda illegal parameter`() {
        assertThrowsMessage<LineException>("illegal lambda parameter") {
            KtParseUtil.parseExpression("on() { x -> 0 }")
        }
    }
}
