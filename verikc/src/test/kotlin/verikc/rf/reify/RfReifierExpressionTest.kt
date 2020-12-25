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

package verikc.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.rf.RfUtil

internal class RfReifierExpressionTest {

    @Test
    fun `function finish`() {
        val string = """
            finish()
        """.trimIndent()
        assertEquals(
            TYPE_REIFIED_UNIT,
            RfUtil.reifyExpression("", string).typeReified
        )
    }

    @Test
    fun `function sbit type class mismatch`() {
        val string = """
            _sbit(_int())
        """.trimIndent()
        val message = "type class mismatch when resolving argument 1 of function $FUNCTION_TYPE_SBIT"
        assertThrowsMessage<LineException>(message) {
            RfUtil.reifyExpression("", string)
        }
    }

    @Test
    fun `operator forever`() {
        val string = """
            forever {}
        """.trimIndent()
        assertEquals(
            TYPE_REIFIED_UNIT,
            RfUtil.reifyExpression("", string).typeReified
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
            TypeReified(TYPE_BOOL, INSTANCE, listOf()),
            RfUtil.reifyExpression(moduleContext, string).typeReified
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
            TypeReified(TYPE_SBIT, INSTANCE, listOf(8)),
            RfUtil.reifyExpression(moduleContext, string).typeReified
        )
    }

    @Test
    fun `string literal`() {
        val string = """
            ""
        """.trimIndent()
        assertEquals(
            TypeReified(TYPE_STRING, INSTANCE, listOf()),
            RfUtil.reifyExpression("", string).typeReified
        )
    }
}
