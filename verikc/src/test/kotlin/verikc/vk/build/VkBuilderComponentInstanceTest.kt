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
import verikc.lang.LangSymbol.FUNCTION_POSEDGE_BOOL
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkComponentInstance
import verikc.vk.ast.VkExpressionFunction
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkProperty

internal class VkBuilderComponentInstanceTest {

    @Test
    fun `module simple`() {
        val fileContext = """
            class N : Module()
        """.trimIndent()
        val string = """
            @make val n = N()
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(5), "n", Symbol(11), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
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
    fun `clock port simple`() {
        val fileContext = """
            class _cp: _clockport()
        """.trimIndent()
        val string = """
            @make val cp = _cp() with {
                on (posedge(false)) {}
            }
        """.trimIndent()
        val expected = VkComponentInstance(
            VkProperty(line(5), "cp", Symbol(11), MutabilityType.VAL, Symbol(3).toTypeGenerified()),
            VkExpressionFunction(
                line(6),
                TYPE_EVENT.toTypeGenerified(),
                FUNCTION_POSEDGE_BOOL,
                null,
                listOf(VkExpressionLiteral(line(6), TYPE_BOOL.toTypeGenerified(), LiteralValue.fromBoolean(false)))
            ),
            listOf(),
            ComponentType.CLOCKPORT
        )
        assertEquals(
            expected,
            VkBuildUtil.buildModuleComponentInstance(fileContext, "", string)
        )
    }
}
