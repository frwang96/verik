/*
 * Copyright 2020 Francis Wang
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

package verik.core.it.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class ItExpressionReifierTest {

    @Test
    fun `function finish`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_UNIT,
                null,
                FUNCTION_FINISH,
                null,
                listOf()
        )
        ItExpressionReifier.reifyExpression(expression)
        assertEquals(TYPE_REIFIED_UNIT, expression.typeReified)
    }

    @Test
    fun `literal bool`() {
        val expression = ItExpressionLiteral(
                0,
                TYPE_BOOL,
                null,
                "false"
        )
        ItExpressionReifier.reifyExpression(expression)
        assertEquals(
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                expression.typeReified
        )
    }
}