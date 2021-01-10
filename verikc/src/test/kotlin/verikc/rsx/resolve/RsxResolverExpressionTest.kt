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

package verikc.rsx.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.kt.KtParseUtil
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rsx.RsxResolveUtil
import verikc.rsx.ast.RsxExpressionProperty

internal class RsxResolverExpressionTest {

    @Test
    fun `function finish`() {
        val string = """
            finish()
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            RsxResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `operator on`() {
        val string = """
            on () {}
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            RsxResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `property int`() {
        val fileContext = """
            val x = _int()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        val property = KtParseUtil.parseProperty(fileContext)
        val expression = RsxResolveUtil.resolveExpression(fileContext, string)
        assertEquals(
            property.symbol,
            (expression as RsxExpressionProperty).propertySymbol
        )
    }

    @Test
    fun `string literal`() {
        val string = """
            ""
        """.trimIndent()
        assertEquals(
            TYPE_STRING.toTypeGenerified(),
            RsxResolveUtil.resolveExpression("", string).typeGenerified
        )
    }
}