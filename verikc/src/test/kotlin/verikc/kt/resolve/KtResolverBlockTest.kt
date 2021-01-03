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

package verikc.kt.resolve

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.kt.KtResolveUtil
import verikc.kt.ast.KtExpressionOperator
import verikc.kt.ast.KtExpressionProperty
import verikc.kt.ast.KtStatementDeclaration
import verikc.kt.ast.KtStatementExpression

internal class KtResolverBlockTest {

    @Test
    fun `property in block`() {
        val string = """
            if (true) {
                val x = 0
                x
            }
        """.trimIndent()
        val expression = KtResolveUtil.resolveExpression("", string)
        val block = (expression as KtExpressionOperator).blocks[0]
        val declaration = block.statements[0] as KtStatementDeclaration
        val property = (block.statements[1] as KtStatementExpression).expression as KtExpressionProperty
        Assertions.assertEquals(
            declaration.primaryProperty.symbol,
            property.propertySymbol
        )
    }
}