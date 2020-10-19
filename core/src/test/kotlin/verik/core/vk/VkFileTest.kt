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

package verik.core.vk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.Symbol

internal class VkFileTest {

    @Test
    fun `file empty`() {
        val string = "package base"
        val file = VkUtil.parseFile(string)
        val expected = VkFile(
                Symbol(1, 1, 0),
                listOf()
        )
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
                Symbol(1, 1, 0),
                listOf(VkModule(
                        2,
                        "_m",
                        Symbol(1, 1, 1),
                        listOf(),
                        false,
                        listOf(),
                        listOf(),
                        listOf()
                ))
        )
        assertEquals(expected, file)
    }
}