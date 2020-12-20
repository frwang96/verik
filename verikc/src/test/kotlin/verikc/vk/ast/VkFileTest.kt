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

package verikc.vk.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.vk.VkUtil

internal class VkFileTest {

    @Test
    fun `file empty`() {
        val string = "package base"
        val file = VkUtil.parseFile(string)
        val expected = VkFile(Symbol(2), listOf())
        assertEquals(expected, file)
    }

    @Test
    fun `file with module`() {
        val string = """
            package base
            class _m: _module
        """.trimIndent()
        val file = VkUtil.parseFile(string)
        val expected = VkFile(
            Symbol(2),
            listOf(
                VkModule(
                    Line(2),
                    "_m",
                    Symbol(3),
                    listOf(),
                    false,
                    listOf(),
                    listOf(),
                    listOf()
                )
            )
        )
        assertEquals(expected, file)
    }
}
