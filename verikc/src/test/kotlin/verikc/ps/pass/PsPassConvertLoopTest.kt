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
import verikc.lang.LangSymbol.OPERATOR_INTERNAL_FOR
import verikc.ps.PsPassUtil
import verikc.ps.ast.PsExpressionOperator

internal class PsPassConvertLoopTest {

    @Test
    fun `convert for range`() {
        val string = """
            for (i in range(8))
        """.trimIndent()
        val expression = PsPassUtil.passModuleActionBlockExpression("", "", string)
        assertEquals(
            OPERATOR_INTERNAL_FOR,
            (expression as PsExpressionOperator).operatorSymbol
        )
    }
}