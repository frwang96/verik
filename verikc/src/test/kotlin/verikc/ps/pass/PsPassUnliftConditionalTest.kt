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
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_WHEN_BODY
import verikc.lang.LangSymbol.OPERATOR_WHEN_WRAPPER
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionOperator

internal class PsPassUnliftConditionalTest {

    @Test
    fun `unlift if else`() {
        val moduleContext = """
            var x = t_Int()
        """.trimIndent()
        val string = """
            x = if (true) 1 else {
                0
                0
            }
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        assertEquals(OPERATOR_IF_ELSE, (expression as PsExpressionOperator).operatorSymbol)
    }

    @Test
    fun `unlift if else nested`() {
        val moduleContext = """
            var x = t_Int()
        """.trimIndent()
        val string = """
            x = if (true) 1 else if (true) 1 else {
                0
                0
            }
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        val nestedExpression = (expression as PsExpressionOperator).blocks[1].expressions[0]
        assertEquals(OPERATOR_IF_ELSE, (nestedExpression as PsExpressionOperator).operatorSymbol)
    }

    @Test
    fun `unlift when body`() {
        val moduleContext = """
            var x = t_Int()
        """.trimIndent()
        val string = """
            x = when {
                true -> 1
                false -> 0
            }
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        assertEquals(OPERATOR_WHEN_BODY, (expression as PsExpressionOperator).operatorSymbol)
    }

    @Test
    fun `unlift when wrapper body`() {
        val moduleContext = """
            var x = t_Int()
        """.trimIndent()
        val string = """
            x = when(x) {
                0 -> 1
                else -> 0
            }
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", moduleContext, string)
        assertEquals(OPERATOR_WHEN_WRAPPER, (expression as PsExpressionOperator).operatorSymbol)
    }
}