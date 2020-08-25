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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.LiteralValue
import verik.core.kt.KtExpressionLiteral
import verik.core.kt.KtStatementExpression
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtStatementParserTest {

    @Test
    fun `expression simple`() {
        val statement = KtUtil.parseStatement("0")
        val expected = KtStatementExpression(1, KtExpressionLiteral(
                1,
                TYPE_INT,
                LiteralValue.fromIntImplicit(0)
        ))
        assertEquals(expected, statement)
    }
}