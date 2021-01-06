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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtBlock
import verikc.kt.ast.KtExpressionOperator
import verikc.kt.ast.KtStatementExpression
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserBlockTest {

    @Test
    fun `block simple`() {
        val string = "forever { 0 }"
        val expected = KtExpressionOperator(
            line(3),
            LangSymbol.OPERATOR_FOREVER,
            null,
            listOf(),
            listOf(
                KtBlock(
                    line(3),
                    Symbol(5),
                    listOf(),
                    listOf(KtStatementExpression.wrapLiteral(line(3), TYPE_INT, LiteralValue.fromInt(0)))
                )
            )
        )
        assertEquals(expected, KtParseUtil.parseExpression(string))
    }
}
