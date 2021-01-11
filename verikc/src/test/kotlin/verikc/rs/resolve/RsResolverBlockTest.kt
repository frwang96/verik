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

package verikc.rs.resolve

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsExpressionOperator
import verikc.rs.ast.RsExpressionProperty
import verikc.rs.ast.RsStatementDeclaration
import verikc.rs.ast.RsStatementExpression

internal class RsResolverBlockTest {

    @Test
    fun `property in block`() {
        val string = """
            if (true) {
                val x = 0
                x
            }
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        val block = (expression as RsExpressionOperator).blocks[0]
        val propertyDeclaration = block.statements[0] as RsStatementDeclaration
        val propertyExpression = (block.statements[1] as RsStatementExpression).expression as RsExpressionProperty
        Assertions.assertEquals(
            propertyDeclaration.property.symbol,
            propertyExpression.propertySymbol
        )
    }

    @Test
    fun `property in block illegal`() {
        val string = """
            if (true) {
                val x = _bool()
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("type expression not permitted") {
            RsResolveUtil.resolveExpression("", string)
        }
    }
}