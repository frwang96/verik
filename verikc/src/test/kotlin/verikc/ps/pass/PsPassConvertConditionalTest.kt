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

package verikc.ps.pass

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_IF_ELSE
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionFunction

internal class PsPassConvertConditionalTest {

    @Test
    fun `convert if else`() {
        val moduleContext = """
            var x = _int()
        """.trimIndent()
        val string = """
            x = if (true) 1 else 0
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        assertEquals(
            FUNCTION_INTERNAL_IF_ELSE,
            ((expression as PsExpressionFunction).args[0] as PsExpressionFunction).functionSymbol
        )
    }

    @Test
    fun `convert if else unable to unlift`() {
        val moduleContext = """
            var x = _int()
        """.trimIndent()
        val string = """
            x = if (true) 1 else {
                0
                0
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("unable to unlift conditional") {
            PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        }
    }
}