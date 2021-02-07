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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionLiteral
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtTypeAlias
import verikc.line

internal class KtParserTypeAliasTest {

    @Test
    fun `type alias simple`() {
        val string = "@alias fun t_Byte() = t_Ubit(8)"
        val expected = KtTypeAlias(
            line(2),
            "Byte",
            Symbol(3),
            KtFunction(line(2), "t_Byte", Symbol(4), listOf(), listOf(), "Byte", listOf(), null),
            KtExpressionFunction(line(2), "t_Ubit", null, null, listOf(KtExpressionLiteral(line(2), "8")))
        )
        Assertions.assertEquals(expected, KtParseUtil.parseTypeAlias(string))
    }

    @Test
    fun `type alias illegal identifier`() {
        val string = "@alias fun Byte() = t_Ubit(8)"
        assertThrowsMessage<LineException>("type constructor should be prefixed with t_") {
            KtParseUtil.parseTypeAlias(string)
        }
    }
}