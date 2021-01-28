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
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_POSEDGE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.*

internal class VkBuilderComponentInstanceTest {

    @Test
    fun `module simple`() {
        val fileContext = """
            class N : Module()
        """.trimIndent()
        val string = """
            @make val n = t_N()
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(5), "n", Symbol(10), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            null,
            null,
            listOf(),
            ComponentType.MODULE
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `module with connection`() {
        val fileContext = """
            class N : Module() {
                @input var x = t_Boolean()
            }
        """.trimIndent()
        val moduleContext = """
            var x = t_Boolean()
        """.trimIndent()
        val string = """
            @make val n = t_N().with(x = x)
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(7), "n", Symbol(12), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            null,
            listOf("x"),
            listOf(
                VkConnection(
                    line(7),
                    VkExpressionProperty(line(7), TYPE_BOOLEAN.toTypeGenerified(), Symbol(11), null),
                    "x",
                    Symbol(6),
                    PortType.INPUT
                )
            ),
            ComponentType.MODULE
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, moduleContext, string)
        )
    }

    @Test
    fun `module with connection null`() {
        val fileContext = """
            class N : Module() {
                @output var x = t_Boolean()
            }
        """.trimIndent()
        val string = """
            @make val n = t_N().with(null)
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(7), "n", Symbol(11), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            null,
            null,
            listOf(VkConnection(line(7), null, null, Symbol(6), PortType.OUTPUT)),
            ComponentType.MODULE
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }

    @Test
    fun `clock port simple`() {
        val fileContext = """
            class CP: ClockPort()
        """.trimIndent()
        val string = """
            @make val cp = t_CP().with(posedge(false))
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(5), "cp", Symbol(10), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            VkExpressionFunction(
                line(5),
                TYPE_EVENT.toTypeGenerified(),
                FUNCTION_POSEDGE_BOOLEAN,
                null,
                listOf(VkExpressionLiteral(line(5), TYPE_BOOLEAN.toTypeGenerified(), LiteralValue.encodeBoolean(false)))
            ),
            null,
            listOf(),
            ComponentType.CLOCK_PORT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }
}
