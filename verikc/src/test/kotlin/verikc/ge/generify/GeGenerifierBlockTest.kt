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

package verikc.ge.generify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.ge.GeGenerifyUtil
import verikc.ge.ast.GeExpressionOperator
import verikc.ge.ast.GeStatementExpression
import verikc.lang.LangSymbol.TYPE_INT

internal class GeGenerifierBlockTest {

    @Test
    fun `property in block`() {
        val string = """
            if (true) {
                val x = 0
                x
            }
        """.trimIndent()
        val expression = GeGenerifyUtil.generifyExpression("", string)
        val block = (expression as GeExpressionOperator).blocks[0]
        assertEquals(
            TYPE_INT.toTypeGenerifiedInstance(),
            (block.statements.last() as GeStatementExpression).expression.typeGenerified
        )
    }

    @Test
    fun `property in block illegal`() {
        val string = """
            if (true) {
                val x = _bool()
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("property should be initialized") {
            GeGenerifyUtil.generifyExpression("", string)
        }
    }
}