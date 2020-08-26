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

package verik.core.lang

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.base.LiteralValue
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItReifiedType
import verik.core.it.ItTypeClass
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_FINISH
import verik.core.lang.LangSymbol.FUNCTION_SINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_SINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.SvStatementExpression

internal class LangFunctionTableTest {

    @Test
    fun `reify bool function`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_BOOL,
                null,
                FUNCTION_BOOL,
                null,
                listOf()
        )
        Lang.functionTable.reify(expression)
        assertEquals(
                ItReifiedType(TYPE_BOOL, ItTypeClass.TYPE, listOf()),
                expression.reifiedType
        )
    }

    @Test
    fun `reify sint function`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_SINT,
                null,
                FUNCTION_SINT,
                null,
                listOf(ItExpressionLiteral(
                        0,
                        TYPE_INT,
                        ItReifiedType(TYPE_INT, ItTypeClass.INSTANCE, listOf()),
                        LiteralValue.fromIntImplicit(8)
                ))
        )
        Lang.functionTable.reify(expression)
        assertEquals(
                ItReifiedType(TYPE_SINT, ItTypeClass.TYPE, listOf(8)),
                expression.reifiedType
        )
    }

    @Test
    fun `extract function finish`() {
        val expression = ItExpressionFunction(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                FUNCTION_FINISH,
                null,
                listOf()
        )
        val request = LangFunctionExtractorRequest(expression, null, listOf())
        val expected = SvStatementExpression.wrapFunction(
                0,
                null,
                "\$finish",
                listOf()
        )
        assertEquals(expected, Lang.functionTable.extract(request))
    }
}