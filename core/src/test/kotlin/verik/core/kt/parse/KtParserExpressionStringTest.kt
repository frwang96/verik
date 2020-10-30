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
import verik.core.kt.*
import verik.core.kt.ast.*

internal class KtParserExpressionStringTest {

    @Test
    fun `literal simple`() {
        val expression = KtUtil.parseExpression("\"x\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentLiteral(1, "x"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `reference simple`() {
        val expression = KtUtil.parseExpression("\"\$x\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentExpression(
                        1,
                        KtStringSegmentExpressionBase.DEFAULT,
                        KtExpressionProperty(1, null, "x", null, null)
                ))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `expression simple`() {
        val expression = KtUtil.parseExpression("\"\${x}\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentExpression(
                        1,
                        KtStringSegmentExpressionBase.DEFAULT,
                        KtExpressionProperty(1, null, "x", null, null)
                ))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence`() {
        val expression = KtUtil.parseExpression("\"\\n\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentLiteral(1, "\\n"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence converted`() {
        val expression = KtUtil.parseExpression("\"\\'\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentLiteral(1, "'"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `escape sequence fuse`() {
        val expression = KtUtil.parseExpression("\"\\n\\n\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentLiteral(1, "\\n\\n"))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `bin reference`() {
        val expression = KtUtil.parseExpression("\"0b\$x\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentExpression(
                        1,
                        KtStringSegmentExpressionBase.BIN,
                        KtExpressionProperty(1, null, "x", null, null)
                ))
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `hex reference`() {
        val expression = KtUtil.parseExpression("\"0X\$x\"")
        val expected = KtExpressionString(
                1,
                null,
                listOf(KtStringSegmentExpression(
                        1,
                        KtStringSegmentExpressionBase.HEX,
                        KtExpressionProperty(1, null, "x", null, null)
                ))
        )
        Assertions.assertEquals(expected, expression)
    }
}
