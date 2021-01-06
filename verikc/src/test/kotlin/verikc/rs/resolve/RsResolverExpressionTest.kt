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

package verikc.rs.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpressionProperty

internal class RsResolverExpressionTest {

    @Test
    fun `function bool type`() {
        val string = """
            _bool()
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_BOOL, expression.typeSymbol)
    }

    @Test
    fun `function ubit type`() {
        val string = """
            _ubit(8)
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_UBIT, expression.typeSymbol)
    }

    @Test
    fun `function regular`() {
        val fileContext = """
            fun g(): _int {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string)
        assertEquals(TYPE_INT, expression.typeSymbol)
    }

    @Test
    fun `function type constructor`() {
        val fileContext = """
            class _m: _module()
        """.trimIndent()
        val string = """
            _m()
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string)
        assertEquals(Symbol(3), expression.typeSymbol)
    }

    @Test
    fun `operator on`() {
        val string = """
            on () {}
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_UNIT, expression.typeSymbol)
    }

    @Test
    fun `property simple`() {
        val fileContext = """
            val x = _int()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string)
        assertEquals(
            Symbol(3),
            (expression as RsExpressionProperty).propertySymbol
        )
    }

    @Test
    fun `property in type`() {
        val fileContext = """
            class _m: _module() {
                val x = 0
            }
        """.trimIndent()
        val string = """
            _m().x
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string)
        assertEquals(TYPE_INT, expression.typeSymbol)
    }

    @Test
    fun `property with enum entry`() {
        val fileContext = """
            enum class _op(val value: _int) {
                ADD(0), SUB(1)
            }
        """.trimIndent()
        val string = """
            _op.ADD
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string)
        assertEquals(Symbol(3), expression.typeSymbol)
    }

    @Test
    fun `string literal`() {
        val string = """
            "0"
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(LangSymbol.TYPE_STRING, expression.typeSymbol)
    }

    @Test
    fun `literal bool`() {
        val string = """
            true
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_BOOL, expression.typeSymbol)
    }

    @Test
    fun `literal int`() {
        val string = """
            0
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        assertEquals(TYPE_INT, expression.typeSymbol)
    }
}
