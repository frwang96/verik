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

package verikc.ps.pass

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionFunction

internal class PsPassAssignmentTest {

    @Test
    fun `pass seq block`() {
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @seq fun f() {
                on(posedge(false)) {
                    x = false
                }
            }
        """.trimIndent()
        val actionBlock = PsPassUtil.passModuleActionBlock("", moduleContext, string)
        val expression = actionBlock.block.expressions[0]
        assertEquals(
            FUNCTION_NATIVE_ASSIGN_NONBLOCKING,
            (expression as PsExpressionFunction).functionSymbol
        )
    }

    @Test
    fun `pass seq block with local property`() {
        val string = """
            @seq fun f() {
                on(posedge(false)) {
                    var x = 0
                }
            }
        """.trimIndent()
        val actionBlock = PsPassUtil.passModuleActionBlock("", "", string)
        val expression = actionBlock.block.expressions[0]
        assertEquals(
            FUNCTION_NATIVE_ASSIGN_BLOCKING,
            (expression as PsExpressionFunction).functionSymbol
        )
    }

    @Test
    fun `pass com block`() {
        val moduleContext = """
            var x = _bool()
        """.trimIndent()
        val string = """
            @com fun f() {
                x = false
            }
        """.trimIndent()
        val actionBlock = PsPassUtil.passModuleActionBlock("", moduleContext, string)
        val expression = actionBlock.block.expressions[0]
        assertEquals(
            FUNCTION_NATIVE_ASSIGN_BLOCKING,
            (expression as PsExpressionFunction).functionSymbol
        )
    }
}
