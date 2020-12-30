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
import verikc.base.ast.BaseType
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionProperty
import verikc.kt.ast.KtExpressionString
import verikc.kt.ast.KtStringSegmentExpression
import verikc.kt.ast.KtStringSegmentLiteral
import verikc.line

internal class KtParserExpressionStringTest {

    @Test
    fun `literal simple`() {
        val expression = KtParseUtil.parseExpression("\"x\"")
        val expected = KtExpressionString(
            line(3),
            null,
            listOf(KtStringSegmentLiteral(line(3), "x"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `reference simple`() {
        val expression = KtParseUtil.parseExpression("\"\$x\"")
        val expected = KtExpressionString(
            line(3),
            null,
            listOf(
                KtStringSegmentExpression(
                    line(3),
                    BaseType.DEFAULT,
                    KtExpressionProperty(line(3), null, "x", null, null)
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `expression simple`() {
        val expression = KtParseUtil.parseExpression("\"\${x}\"")
        val expected = KtExpressionString(
            line(3),
            null,
            listOf(
                KtStringSegmentExpression(
                    line(3),
                    BaseType.DEFAULT,
                    KtExpressionProperty(line(3), null, "x", null, null)
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence`() {
        val expression = KtParseUtil.parseExpression("\"\\n\"")
        val expected = KtExpressionString(line(3), null, listOf(KtStringSegmentLiteral(line(3), "\\n")))
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence converted`() {
        val expression = KtParseUtil.parseExpression("\"\\'\"")
        val expected = KtExpressionString(line(3), null, listOf(KtStringSegmentLiteral(line(3), "'")))
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence fuse`() {
        val expression = KtParseUtil.parseExpression("\"\\n\\n\"")
        val expected = KtExpressionString(line(3), null, listOf(KtStringSegmentLiteral(line(3), "\\n\\n")))
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `bin reference`() {
        val expression = KtParseUtil.parseExpression("\"0b\$x\"")
        val expected = KtExpressionString(
            line(3),
            null,
            listOf(
                KtStringSegmentExpression(
                    line(3),
                    BaseType.BIN,
                    KtExpressionProperty(line(3), null, "x", null, null)
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `hex reference`() {
        val expression = KtParseUtil.parseExpression("\"0X\$x\"")
        val expected = KtExpressionString(
            line(3),
            null,
            listOf(
                KtStringSegmentExpression(
                    line(3),
                    BaseType.HEX,
                    KtExpressionProperty(line(3), null, "x", null, null)
                )
            )
        )
        Assertions.assertEquals(expected, expression)
    }
}
