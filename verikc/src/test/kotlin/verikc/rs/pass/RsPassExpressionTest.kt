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

package verikc.rs.pass

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpressionOperator
import verikc.rs.ast.RsStatementExpression

internal class RsPassExpressionTest {

    @Test
    fun `function finish`() {
        val string = """
            finish()
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            RsResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function boolean type`() {
        val string = """
            t_Boolean()
        """.trimIndent()
        assertEquals(
            TYPE_BOOLEAN.toTypeGenerified(),
            RsResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function ubit type`() {
        val string = """
            t_Ubit(8)
        """.trimIndent()
        assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function simple`() {
        val fileContext = """
            fun g(): Int {}
        """.trimIndent()
        val string = """
            g()
        """.trimIndent()
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `function type constructor`() {
        val fileContext = """
            class M : Module()
        """.trimIndent()
        val string = """
            t_M()
        """.trimIndent()
        assertEquals(
            Symbol(3).toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `function instance constructor`() {
        val fileContext = """
            class C: Class()
        """.trimIndent()
        val string = """
            i_C()
        """.trimIndent()
        assertEquals(
            Symbol(3).toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `operator on`() {
        val string = """
            on () {}
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            RsResolveUtil.resolveExpression("", string).typeGenerified
        )
    }

    @Test
    fun `operator with`() {
        val fileContext = """
            class M : Module()
        """.trimIndent()
        val string = """
            t_M() with { it }
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression(fileContext, string) as RsExpressionOperator
        val block = expression.blocks[0]
        assertEquals(
            Symbol(3).toTypeGenerified(),
            (block.statements[0] as RsStatementExpression).expression.typeGenerified
        )
    }

    @Test
    fun `property int`() {
        val fileContext = """
            val x = t_Int()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `property sbit`() {
        val fileContext = """
            val x = t_Sbit(8)
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        assertEquals(
            TYPE_SBIT.toTypeGenerified(8),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `property in type`() {
        val fileContext = """
            class M : Module() {
                val x = 0
            }
        """.trimIndent()
        val string = """
            t_M().x
        """.trimIndent()
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `property with enum entry`() {
        val fileContext = """
            enum class Op(val value: Ubit) {
                ADD(u(0)), SUB(u(1))
            }
        """.trimIndent()
        val string = """
            Op.ADD
        """.trimIndent()
        assertEquals(
            Symbol(3).toTypeGenerified(),
            RsResolveUtil.resolveExpression(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `string literal`() {
        val string = """
            ""
        """.trimIndent()
        assertEquals(
            TYPE_STRING.toTypeGenerified(),
            RsResolveUtil.resolveExpression("", string).typeGenerified
        )
    }
}