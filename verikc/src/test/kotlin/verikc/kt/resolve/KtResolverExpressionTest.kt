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

package verikc.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.kt.KtResolveUtil
import verikc.kt.ast.KtExpressionProperty
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT

internal class KtResolverExpressionTest {

    @Test
    fun `function bool type`() {
        val string = "_bool()"
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_BOOL, expression.typeSymbol)
    }

    @Test
    fun `function ubit type`() {
        val string = "_ubit(1)"
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_UBIT, expression.typeSymbol)
    }

    @Test
    fun `operator on`() {
        val string = "on () {}"
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_UNIT, expression.typeSymbol)
    }

    @Test
    fun `property simple`() {
        val fileContext = """
            val x = _int()
        """.trimIndent()
        val string = "x"
        val expression = KtResolveUtil.resolveExpression(fileContext, string)
        assertEquals(
            Symbol(3),
            (expression as KtExpressionProperty).propertySymbol
        )
    }

    @Test
    fun `string literal`() {
        val string = "\"0\""
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(LangSymbol.TYPE_STRING, expression.typeSymbol)
    }

    @Test
    fun `literal bool`() {
        val string = "true"
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_BOOL, expression.typeSymbol)
    }

    @Test
    fun `literal int`() {
        val string = "0"
        val expression = KtResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_INT, expression.typeSymbol)
    }
}
