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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkProperty

internal class VkBuilderEnumTest {

    @Test
    fun `enum illegal no properties`() {
        val string = "enum class Op"
        assertThrowsMessage<LineException>("expected enum properties") {
            VkBuildUtil.buildEnum("", string)
        }
    }

    @Test
    fun `enum simple`() {
        val string = """
            enum class Op {
                ADD, SUB
            }
        """.trimIndent()
        val expected = VkEnum(
            line(3),
            "Op",
            Symbol(3),
            VkProperty(line(3), "Op", Symbol(4), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            listOf(
                VkProperty(line(4), "ADD", Symbol(6), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
                VkProperty(line(4), "SUB", Symbol(7), MutabilityType.VAL, Symbol(3).toTypeGenerified())
            )
        )
        assertEquals(expected, VkBuildUtil.buildEnum("", string))
    }
}
