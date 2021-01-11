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
import verikc.ge.GeGenerifyUtil
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rsx.RsxResolveUtil
import verikc.rsx.ast.RsxExpressionOperator
import verikc.rsx.ast.RsxStatementExpression

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
    fun `function bool type`() {
        val string = """
            _bool()
        """.trimIndent()
        assertEquals(
            TYPE_BOOL.toTypeGenerified(),
            RsxResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function ubit type`() {
        val string = """
            _ubit(8)
        """.trimIndent()
        assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsxResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function simple`() {
        val fileContext = """
            fun g(): _int {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsxResolveUtil.resolveExpression(fileContext, string).typeGenerified
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
    fun `operator with`() {
        val string = """
            _int() with { it }
        """.trimIndent()
        val expression = RsxResolveUtil.resolveExpression("", string) as RsxExpressionOperator
        val block = expression.blocks[0]
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            (block.statements[0] as RsxStatementExpression).expression.typeGenerified
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
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsxResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `property sbit`() {
        val moduleContext = """
            val x = _sbit(8)
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        assertEquals(
            TYPE_SBIT.toTypeGenerified(8),
            GeGenerifyUtil.generifyExpression(moduleContext, string).typeGenerified
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