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
import verikc.base.ast.LiteralValue
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionLiteral
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserExpressionLiteralTest {

    @Test
    fun `bool false`() {
        val expression = KtParseUtil.parseExpression("false")
        Assertions.assertEquals(
            KtExpressionLiteral(line(3), TYPE_BOOL, LiteralValue.fromBoolean(false)),
            expression
        )
    }

    @Test
    fun `int bin`() {
        val expression = KtParseUtil.parseExpression("0b0000_1111")
        Assertions.assertEquals(
            KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0b0000_1111)),
            expression
        )
    }

    @Test
    fun `int hex`() {
        val expression = KtParseUtil.parseExpression("0X00ff")
        Assertions.assertEquals(
            KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0x00ff)),
            expression
        )
    }

    @Test
    fun `int dec`() {
        val expression = KtParseUtil.parseExpression("3")
        Assertions.assertEquals(
            KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(3)),
            expression
        )
    }

    @Test
    fun `int dec zero`() {
        val expression = KtParseUtil.parseExpression("0")
        Assertions.assertEquals(
            KtExpressionLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0)),
            expression
        )
    }
}
