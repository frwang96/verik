/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionProperty
import verikc.line

internal class KtParserExpressionBaseTest {

    @Test
    fun `disjunction expression`() {
        val expression = KtParseUtil.parseExpression("x \n || y")
        val expected = KtExpressionFunction(
            line(3),
            "||",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(4), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `conjunction expression`() {
        val expression = KtParseUtil.parseExpression("x && \n y")
        val expected = KtExpressionFunction(
            line(3),
            "&&",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(4), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `equality expression`() {
        val expression = KtParseUtil.parseExpression("x == y")
        val expected = KtExpressionFunction(
            line(3),
            "==",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `comparison expression`() {
        val expression = KtParseUtil.parseExpression("x < y")
        val expected = KtExpressionFunction(
            line(3),
            "<",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation in expression`() {
        val expression = KtParseUtil.parseExpression("x in y")
        val expected = KtExpressionFunction(
            line(3),
            "in",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix operation is expression`() {
        val expression = KtParseUtil.parseExpression("x is y")
        val expected = KtExpressionFunction(
            line(3),
            "is",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression`() {
        val expression = KtParseUtil.parseExpression("x con y")
        val expected = KtExpressionFunction(
            line(3),
            "con",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `infix function expression illegal function literal`() {
        assertThrowsMessage<LineException>("lambda literals are not permitted") {
            KtParseUtil.parseExpression("x with {}")
        }
    }

    @Test
    fun `additive operation expression`() {
        val expression = KtParseUtil.parseExpression("x + y")
        val expected = KtExpressionFunction(
            line(3),
            "+",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `multiplicative operation expression`() {
        val expression = KtParseUtil.parseExpression("x * y")
        val expected = KtExpressionFunction(
            line(3),
            "*",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `as expression`() {
        val expression = KtParseUtil.parseExpression("x as y")
        val expected = KtExpressionFunction(
            line(3),
            "as",
            KtExpressionProperty(line(3), "x", null),
            null,
            listOf(KtExpressionProperty(line(3), "y", null))
        )
        Assertions.assertEquals(expected, expression)
    }
}
