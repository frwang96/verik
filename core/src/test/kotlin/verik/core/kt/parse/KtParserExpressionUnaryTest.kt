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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.base.ast.Symbol
import verik.core.kt.*
import verik.core.kt.ast.*
import verik.core.lang.LangSymbol.OPERATOR_ON
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtParserExpressionUnaryTest {

    @Test
    fun `prefix unary expression`() {
        val expression = KtUtil.parseExpression("!x")
        val expected = KtExpressionFunction(
                1,
                null,
                "!",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `indexing suffix expression`() {
        val expression = KtUtil.parseExpression("x[0]")
        val expected = KtExpressionFunction(
                1,
                null,
                "get",
                KtExpressionProperty(1, null, "x", null, null),
                listOf(KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromInt(0))),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `navigation suffix expression`() {
        val expression = KtUtil.parseExpression("x.y")
        val expected = KtExpressionProperty(
                1,
                null,
                "y",
                KtExpressionProperty(1, null, "x", null, null),
                null
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression`() {
        val expression = KtUtil.parseExpression("x()")
        val expected = KtExpressionFunction(
                1,
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
        val expression = KtUtil.parseExpression("on() {}")
        val expected = KtExpressionOperator(
                1,
                null,
                OPERATOR_ON,
                null,
                listOf(),
                listOf(KtBlock(1, Symbol(1, 1, 1), listOf(), listOf()))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `call suffix expression with lambda illegal parameter`() {
        assertThrowsMessage<LineException>("illegal lambda parameter") {
            KtUtil.parseExpression("on() { x -> 0 }")
        }
    }
}