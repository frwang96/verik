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

package verikc.rs.pass

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.rs.RsResolveUtil
import verikc.rs.ast.RsStatementExpression

internal class RsPassPropertyBaseTest {

    @Test
    fun `property this`() {
        val string = """
            fun f() {
                this
            }
        """.trimIndent()
        val function = RsResolveUtil.resolveTypeFunction("", "", string)
        val statement = function.block.statements[0]
        val expression = (statement as RsStatementExpression).expression
        Assertions.assertEquals(
            Symbol(3).toTypeGenerified(),
            expression.getTypeGenerifiedNotNull()
        )
    }

    @Test
    fun `property null`() {
        val string = """
            null
        """.trimIndent()
        val expression = RsResolveUtil.resolveExpression("", string)
        Assertions.assertEquals(
            TYPE_ANY.toTypeGenerified(),
            expression.getTypeGenerifiedNotNull()
        )
    }
}