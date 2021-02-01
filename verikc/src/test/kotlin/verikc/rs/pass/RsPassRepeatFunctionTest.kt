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
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rs.RsResolveUtil

internal class RsPassRepeatFunctionTest {

    @Test
    fun `function simple`() {
        val string = "fun f() {}"
        assertEquals(
            TYPE_UNIT.toTypeGenerified(),
            RsResolveUtil.resolveFunction("", string).returnTypeGenerified
        )
    }

    @Test
    fun `function parameter ubit`() {
        val string = """
            fun f(x: Ubit) {
                type(x, t_Ubit(8))
            }
        """.trimIndent()
        assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveFunction("", string).parameterProperties[0].typeGenerified
        )
    }

    @Test
    fun `function parameter ubit with property`() {
        val fileContext = """
            val WIDTH = 8
        """.trimIndent()
        val string = """
            fun f(x: Ubit) {
                type(x, t_Ubit(WIDTH))
            }
        """.trimIndent()
        assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveFunction(fileContext, string).parameterProperties[0].typeGenerified
        )
    }

    @Test
    fun `function parameter ubit undefined type`() {
        val string = """
            fun f(x: Ubit) {}
        """.trimIndent()
        assertThrowsMessage<LineException>("type function expected for function parameter [[4]]") {
            RsResolveUtil.resolveFunction("", string)
        }
    }

    @Test
    fun `function return ubit`() {
        val string = """
            fun f(): Ubit {
                type(t_Ubit(8))
                return u(8, 0)
            }
        """.trimIndent()
        assertEquals(
            TYPE_UBIT.toTypeGenerified(8),
            RsResolveUtil.resolveFunction("", string).returnTypeGenerified
        )
    }

    @Test
    fun `function return int already defined`() {
        val string = """
            fun f(): Int {
                type(t_Int())
                return 0
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("function return value has already been assigned a type") {
            RsResolveUtil.resolveFunction("", string)
        }
    }

    @Test
    fun `function return ubit undefined type`() {
        val string = """
            fun f(): Ubit {
                return u(8, 0)
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("type function expected for function return value") {
            RsResolveUtil.resolveFunction("", string)
        }
    }
}
