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

package verikc.vk.ast

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.ConnectionType
import verikc.base.ast.Line
import verikc.base.ast.Symbol
import verikc.kt.ast.KtExpressionProperty
import verikc.kt.ast.KtStatementExpression
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_CON
import verikc.lang.LangSymbol.TYPE_UNIT

internal class VkConnectionTest {

    @Test
    fun `input connection`() {
        val itExpression = KtExpressionProperty(Line(1), null, "it", null, Symbol(3))
        val statement = KtStatementExpression.wrapFunction(
            Line(0),
            TYPE_UNIT,
            "+=",
            KtExpressionProperty(Line(0), null, "x", itExpression, Symbol(4)),
            listOf(KtExpressionProperty(Line(0), null, "y", null, Symbol(5))),
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        )
        val expected = VkConnection(
            Line(0),
            Symbol(4),
            Symbol(5),
            ConnectionType.INPUT
        )
        assertEquals(expected, VkConnection(statement, Symbol(3)))
    }

    @Test
    fun `output connection`() {
        val itExpression = KtExpressionProperty(Line(1), null, "it", null, Symbol(3))
        val statement = KtStatementExpression.wrapFunction(
            Line(0),
            TYPE_UNIT,
            "+=",
            KtExpressionProperty(Line(0), null, "x", null, Symbol(4)),
            listOf(KtExpressionProperty(Line(0), null, "y", itExpression, Symbol(5))),
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        )
        val expected = VkConnection(
            Line(0),
            Symbol(5),
            Symbol(4),
            ConnectionType.OUTPUT
        )
        assertEquals(expected, VkConnection(statement, Symbol(3)))
    }

    @Test
    fun `inout connection`() {
        val itExpression = KtExpressionProperty(Line(1), null, "it", null, Symbol(3))
        val statement = KtStatementExpression.wrapFunction(
            Line(0),
            TYPE_UNIT,
            "con",
            KtExpressionProperty(Line(0), null, "x", itExpression, Symbol(4)),
            listOf(KtExpressionProperty(Line(0), null, "y", null, Symbol(5))),
            FUNCTION_CON
        )
        val expected = VkConnection(
            Line(0),
            Symbol(4),
            Symbol(5),
            ConnectionType.INOUT
        )
        assertEquals(expected, VkConnection(statement, Symbol(3)))
    }
}
