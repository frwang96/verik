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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.MethodBlockType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkBlock
import verikc.vk.ast.VkMethodBlock

internal class VkBuilderMethodBlockTest {

    @Test
    fun `function simple`() {
        val string = """
            fun f() {}
        """.trimIndent()
        val expected = VkMethodBlock(
            line(4),
            "f",
            Symbol(6),
            MethodBlockType.FUNCTION,
            listOf(),
            TYPE_UNIT.toTypeGenerifiedInstance(),
            VkBlock(line(4), listOf(), listOf())
        )
        assertEquals(expected, VkBuildUtil.buildModuleMethodBlock("", string))
    }

    @Test
    fun `task simple`() {
        val string = """
            @task fun f() {}
        """.trimIndent()
        val expected = VkMethodBlock(
            line(4),
            "f",
            Symbol(6),
            MethodBlockType.TASK,
            listOf(),
            TYPE_UNIT.toTypeGenerifiedInstance(),
            VkBlock(line(4), listOf(), listOf())
        )
        assertEquals(expected, VkBuildUtil.buildModuleMethodBlock("", string))
    }

    @Test
    fun `task return not supported`() {
        val string = """
            @task fun f(): _int {
                return 0
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("task return value not supported") {
            VkBuildUtil.buildModuleMethodBlock("", string)
        }
    }
}