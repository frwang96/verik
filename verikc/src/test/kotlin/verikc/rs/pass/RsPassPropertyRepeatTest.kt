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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_INT
import verikc.rs.RsResolveUtil

internal class RsPassPropertyRepeatTest {

    @Test
    fun `property boolean`() {
        val string = "val x = t_Boolean()"
        Assertions.assertEquals(
            TYPE_BOOLEAN.toTypeGenerified(),
            RsResolveUtil.resolveProperty("", string).typeGenerified
        )
    }

    @Test
    fun `property array boolean`() {
        val string = """
            var x = t_Array(8, t_Boolean())
        """.trimIndent()
        Assertions.assertEquals(
            TYPE_ARRAY.toTypeGenerified(8, TYPE_BOOLEAN.toTypeGenerified()),
            RsResolveUtil.resolveProperty("", string).typeGenerified
        )
    }

    @Test
    fun `property multiple passes`() {
        val fileContext = """
            val x = y
            val y = 0
        """.trimIndent()
        val string = """
            var z = x
        """.trimIndent()
        Assertions.assertEquals(
            TYPE_INT.toTypeGenerified(),
            RsResolveUtil.resolveProperty(fileContext, string).typeGenerified
        )
    }

    @Test
    fun `property circular dependency`() {
        val fileContext = """
            val x = y
        """.trimIndent()
        val string = """
            var y = x
        """.trimIndent()
        assertThrowsMessage<LineException>("could not resolve type of [[4]]") {
            println(RsResolveUtil.resolveProperty(fileContext, string))
        }
    }
}