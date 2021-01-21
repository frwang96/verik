/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.resolve

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpressionLiteral

internal class RsResolverLiteralTest {

    @Test
    fun `boolean false`() {
        val expression = RsResolveUtil.resolveExpression("", "false")
        val expected = RsExpressionLiteral(
            line(4),
            TYPE_BOOLEAN.toTypeGenerified(),
            VALUE,
            "false",
            LiteralValue.fromBoolean(false)
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `int bin`() {
        val expression = RsResolveUtil.resolveExpression("", "0b0000_1111")
        val expected = RsExpressionLiteral(
            line(4),
            TYPE_INT.toTypeGenerified(),
            VALUE,
            "0b0000_1111",
            LiteralValue.fromInt(0b0000_1111)
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `int hex`() {
        val expression = RsResolveUtil.resolveExpression("", "0X00ff")
        val expected = RsExpressionLiteral(
            line(4),
            TYPE_INT.toTypeGenerified(),
            VALUE,
            "0X00ff",
            LiteralValue.fromInt(0x00ff)
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `int dec`() {
        val expression = RsResolveUtil.resolveExpression("", "3")
        val expected = RsExpressionLiteral(
            line(4),
            TYPE_INT.toTypeGenerified(),
            VALUE,
            "3",
            LiteralValue.fromInt(3)
        )
        Assertions.assertEquals(expected, expression)
    }

    @Test
    fun `int dec zero`() {
        val expression = RsResolveUtil.resolveExpression("", "0")
        val expected = RsExpressionLiteral(
            line(4),
            TYPE_INT.toTypeGenerified(),
            VALUE,
            "0",
            LiteralValue.fromInt(0)
        )
        Assertions.assertEquals(expected, expression)
    }
}
