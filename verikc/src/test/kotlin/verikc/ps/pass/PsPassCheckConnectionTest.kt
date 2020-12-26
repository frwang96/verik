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

package verikc.ps.pass

import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.ps.PsPassUtil

internal class PsPassCheckConnectionTest {

    @Test
    fun `connections valid`() {
        val fileContext = """
            class _n: _module {
                @input var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        PsPassUtil.passComponentInstance(fileContext, moduleContext, string)
    }

    @Test
    fun `connection duplicate`() {
        val fileContext = """
            class _n: _module {
                @input var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("duplicate connection [[6]]") {
            PsPassUtil.passComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `connections invalid`() {
        val fileContext = """
            class _n: _module {
                var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("invalid connection [[6]]") {
            PsPassUtil.passComponentInstance(fileContext, moduleContext, string)
        }
    }

    @Test
    fun `connection missing`() {
        val fileContext = """
            class _n: _module {
                @input var x = _bool()
            }
        """.trimIndent()
        val string = """
            @make val n = _n() with {}
        """.trimIndent()
        assertThrowsMessage<LineException>("missing connection [[6]]") {
            PsPassUtil.passComponentInstance(fileContext, "", string)
        }
    }

    @Test
    fun `connections type mismatch`() {
        val fileContext = """
            class _n: _module {
                @output var x = _bool()
            }
        """.trimIndent()
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @make val n = _n() with {
                it.x = x
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("output assignment expected for [[6]]") {
            PsPassUtil.passComponentInstance(fileContext, moduleContext, string)
        }
    }
}
