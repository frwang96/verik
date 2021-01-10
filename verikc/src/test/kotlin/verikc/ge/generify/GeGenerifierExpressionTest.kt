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

package verikc.ge.generify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.ge.GeGenerifyUtil
import verikc.ge.ast.GeExpressionOperator
import verikc.ge.ast.GeStatementExpression

internal class GeGenerifierExpressionTest {

    @Test
    fun `operator with`() {
        val fileContext = """
            class _m: _module()
        """.trimIndent()
        val string = """
            _m() with { it }
        """.trimIndent()
        val expression = GeGenerifyUtil.generifyExpression(fileContext, string) as GeExpressionOperator
        val block = expression.blocks[0]
        assertEquals(
            Symbol(3).toTypeGenerified(),
            (block.statements[0] as GeStatementExpression).expression.typeGenerified
        )
    }
}
