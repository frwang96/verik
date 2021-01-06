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
import verikc.assertThrowsMessage
import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.KtExpressionLiteral
import verikc.kt.ast.KtPrimaryProperty
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line

internal class KtParserPropertyTest {

    @Test
    fun `primary property annotation`() {
        val string = "@input val x = 0"
        val declaration = KtParseUtil.parseDeclaration(string) as KtPrimaryProperty
        assertEquals(listOf(AnnotationProperty.INPUT), declaration.annotations)
    }

    @Test
    fun `primary property annotation not supported`() {
        val string = "@x val x = 0"
        assertThrowsMessage<LineException>("annotation x not supported for property declaration") {
            KtParseUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `primary property simple`() {
        val string = "val x = 0"
        val expected = KtPrimaryProperty(
            line(2),
            "x",
            Symbol(3),
            null,
            listOf(),
            KtExpressionLiteral(line(2), TYPE_INT, LiteralValue.fromInt(0))
        )
        assertEquals(expected, KtParseUtil.parseDeclaration(string))
    }

    @Test
    fun `primary property name reserved`() {
        val string = "val always = 0"
        assertThrowsMessage<LineException>("identifier always is reserved in SystemVerilog") {
            KtParseUtil.parseDeclaration(string)
        }
    }
}
