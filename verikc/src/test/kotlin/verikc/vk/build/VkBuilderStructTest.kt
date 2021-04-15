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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.base.ast.MethodBlockType
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkBlock
import verikc.vk.ast.VkMethodBlock
import verikc.vk.ast.VkProperty
import verikc.vk.ast.VkStruct

internal class VkBuilderStructTest {

    @Test
    fun `struct simple`() {
        val string = """
            class S: Struct() {
                val x = t_Boolean()
            }
        """.trimIndent()
        val expected = VkStruct(
            line(3),
            "S",
            Symbol(3),
            VkMethodBlock(
                line(3),
                "i_S",
                Symbol(7),
                MethodBlockType.INSTANCE_CONSTRUCTOR,
                listOf(VkProperty(line(4), "x", Symbol(8), MutabilityType.VAL, TYPE_BOOLEAN.toTypeGenerified())),
                Symbol(3).toTypeGenerified(),
                VkBlock(line(3), listOf(), listOf(), listOf())
            ),
            listOf(
                VkProperty(line(4), "x", Symbol(6), MutabilityType.VAL, TYPE_BOOLEAN.toTypeGenerified())
            )
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildStruct("", string))
    }
}