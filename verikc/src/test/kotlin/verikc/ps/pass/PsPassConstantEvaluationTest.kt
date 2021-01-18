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
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.TYPE_INT
import verikc.line
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionLiteral

internal class PsPassConstantEvaluationTest {

    @Test
    fun `evaluate function native add int int`() {
        val string = """
            2 + 1
        """.trimIndent()
        val expected = PsExpressionLiteral(line(6), TYPE_INT.toTypeGenerified(), LiteralValue.fromInt(3))
        assertEquals(
            expected,
            PsPassUtil.passComponentActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `evaluate function log int`() {
        val string = """
            log(2)
        """.trimIndent()
        val expected = PsExpressionLiteral(line(6), TYPE_INT.toTypeGenerified(), LiteralValue.fromInt(1))
        assertEquals(
            expected,
            PsPassUtil.passComponentActionBlockExpression("", "", string)
        )
    }

    @Test
    fun `evaluate function multiple`() {
        val string = """
            1 + 2 + 3
        """.trimIndent()
        val expected = PsExpressionLiteral(line(6), TYPE_INT.toTypeGenerified(), LiteralValue.fromInt(6))
        assertEquals(
            expected,
            PsPassUtil.passComponentActionBlockExpression("", "", string)
        )
    }
}