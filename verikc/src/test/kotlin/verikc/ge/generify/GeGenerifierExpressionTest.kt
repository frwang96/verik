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

package verikc.ge.generify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.ge.GeGenerifyUtil
import verikc.ge.ast.GeExpressionOperator
import verikc.ge.ast.GeStatementExpression
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.lang.LangSymbol.TYPE_UNIT

internal class GeGenerifierExpressionTest {

    @Test
    fun `function finish`() {
        val string = """
            finish()
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            GeGenerifyUtil.generifyExpression("", string).typeGenerified
        )
    }

    @Test
    fun `function sbit type class mismatch`() {
        val string = """
            _sbit(_int())
        """.trimIndent()
        val message = "type expression not permitted in argument 1 of function $FUNCTION_TYPE_SBIT"
        assertThrowsMessage<LineException>(message) {
            GeGenerifyUtil.generifyExpression("", string)
        }
    }

    @Test
    fun `operator forever`() {
        val string = """
            forever {}
        """.trimIndent()
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            GeGenerifyUtil.generifyExpression("", string).typeGenerified
        )
    }

    @Test
    fun `operator with`() {
        val string = """
            0 with { it }
        """.trimIndent()
        val expression = GeGenerifyUtil.generifyExpression("", string) as GeExpressionOperator
        val block = expression.blocks[0]
        assertEquals(
            TYPE_INT.toTypeGenerified(),
            (block.statements[0] as GeStatementExpression).expression.typeGenerified
        )
    }

    @Test
    fun `property bool`() {
        val moduleContext = """
            val x = _bool()
        """.trimIndent()
        val string = """
            x
        """.trimIndent()
        assertEquals(
            TYPE_BOOL.toTypeGenerified(),
            GeGenerifyUtil.generifyExpression(moduleContext, string).typeGenerified
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
            GeGenerifyUtil.generifyExpression("", string).typeGenerified
        )
    }
}
