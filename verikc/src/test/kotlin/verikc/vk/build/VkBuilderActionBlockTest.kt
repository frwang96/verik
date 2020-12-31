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
import verikc.base.ast.ActionBlockType
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_POSEDGE_BOOL
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkActionBlock
import verikc.vk.ast.VkBlock
import verikc.vk.ast.VkExpressionFunction
import verikc.vk.ast.VkExpressionLiteral

internal class VkBuilderActionBlockTest {

    @Test
    fun `com action block`() {
        val string = "@com fun f() {}"
        val expected = VkActionBlock(
            line(4),
            "f",
            Symbol(6),
            ActionBlockType.COM,
            listOf(),
            VkBlock(line(4), listOf())
        )
        assertEquals(expected, VkBuildUtil.buildActionBlock("", string))
    }

    @Test
    fun `seq action block`() {
        val string = """
            @seq fun f() {
                on (posedge(false)) {}
            }
        """.trimIndent()
        val expected = VkActionBlock(
            line(4),
            "f",
            Symbol(6),
            ActionBlockType.SEQ,
            listOf(
                VkExpressionFunction(
                    line(5),
                    TYPE_EVENT,
                    FUNCTION_POSEDGE_BOOL,
                    null,
                    listOf(VkExpressionLiteral(line(5), TYPE_BOOL, LiteralValue.fromBoolean(false)))
                )
            ),
            VkBlock(line(5), listOf())
        )
        assertEquals(expected, VkBuildUtil.buildActionBlock("", string))
    }

    @Test
    fun `seq action block no on expression`() {
        val string = """
            @seq fun f() {}
        """.trimIndent()
        assertThrowsMessage<LineException>("on expression expected for seq block") {
            VkBuildUtil.buildActionBlock("", string)
        }
    }

    @Test
    fun `seq action block illegal`() {
        val string = """
            @seq fun f() {
                on (posedge(false)) {}
                0
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("illegal use of on expression") {
            VkBuildUtil.buildActionBlock("", string)
        }
    }

    @Test
    fun `run action block illegal`() {
        val string = """
            @run fun f() {
                on (posedge(false)) {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("on expression not permitted here") {
            VkBuildUtil.buildActionBlock("", string)
        }
    }
}