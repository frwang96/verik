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

package io.verik.core.vk

import io.verik.core.al.AlRuleParser
import io.verik.core.kt.parseFile
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkxFileTest {

    @Test
    fun `file empty`() {
        val rule = AlRuleParser.parseKotlinFile("package x")
        val file = VkxFile(parseFile(rule))
        val expected = VkxFile(
                Symbol(1, 1, 0),
                listOf()
        )
        assertEquals(expected, file)
    }

    @Test
    fun `file with module`() {
        val rule = AlRuleParser.parseKotlinFile("""
            package x
            class _m: _module
        """.trimIndent())
        val file = VkxFile(parseFile(rule))
        val expected = VkxFile(
                Symbol(1, 1, 0),
                listOf(VkxComponent(
                        2,
                        "_m",
                        Symbol(1, 1, 1),
                        VkxComponentType.MODULE,
                        false,
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf()
                ))
        )
        assertEquals(expected, file)
    }
}