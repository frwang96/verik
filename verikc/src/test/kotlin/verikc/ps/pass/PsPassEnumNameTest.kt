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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_NAME_ENUM
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionFunction
import verikc.ps.ast.PsExpressionString
import verikc.ps.ast.PsStringSegmentExpression

internal class PsPassEnumNameTest {

    @Test
    fun `pass print function`() {
        val fileContext = """
            enum class Op { ADD, SUB }
        """.trimIndent()
        val string = """
            print(Op.ADD)
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression(fileContext, "", string)
        Assertions.assertEquals(
            FUNCTION_INTERNAL_NAME_ENUM,
            ((expression as PsExpressionFunction).args[0] as PsExpressionFunction).functionSymbol
        )
    }

    @Test
    fun `pass string segment`() {
        val fileContext = """
            enum class Op { ADD, SUB }
        """.trimIndent()
        val string = """
            "${"\$"}{Op.ADD}"
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression(fileContext, "", string)
        val segment = (expression as PsExpressionString).segments[0]
        Assertions.assertEquals(
            FUNCTION_INTERNAL_NAME_ENUM,
            ((segment as PsStringSegmentExpression).expression as PsExpressionFunction).functionSymbol
        )
    }
}