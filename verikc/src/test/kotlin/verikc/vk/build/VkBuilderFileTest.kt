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
import verikc.base.ast.ComponentType
import verikc.base.ast.LiteralValue
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkComponent
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkPrimaryProperty
import verikc.vk.ast.VkProperty

internal class VkBuilderFileTest {

    @Test
    fun `file with component`() {
        val string = """
            package test
            class _m: _module()
        """.trimIndent()
        val file = VkBuildUtil.buildFile(string)
        val expected = listOf(
            VkComponent(
                line(2),
                "_m",
                Symbol(3),
                ComponentType.MODULE,
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf()
            )
        )
        assertEquals(expected, file.components)
    }

    @Test
    fun `file with primary property`() {
        val string = """
            package test
            val x = 0
        """.trimIndent()
        val file = VkBuildUtil.buildFile(string)
        val expected = listOf(
            VkPrimaryProperty(
                VkProperty(line(2), "x", Symbol(3), MutabilityType.VAL, TYPE_INT.toTypeGenerified()),
                VkExpressionLiteral(line(2), TYPE_INT.toTypeGenerified(), LiteralValue.fromInt(0))
            )
        )
        assertEquals(expected, file.primaryProperties)
    }
}
