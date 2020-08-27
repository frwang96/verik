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

package verik.core.vk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.FUNCTION_CON
import verik.core.lang.LangSymbol.OPERATOR_WITH
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class VkComponentInstanceTest {

    @Test
    fun `without con expression`() {
        val declaration = KtDeclarationPrimaryProperty(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                listOf(KtAnnotationProperty.MAKE),
                KtExpressionFunction(0, Symbol(1, 1, 1), "_m", null, listOf(), null)
        )
        val expected = VkComponentInstance(
                0,
                "m",
                Symbol(1, 1, 2),
                Symbol(1, 1, 1),
                listOf()
        )
        assertEquals(
                expected,
                VkComponentInstance(declaration)
        )
    }

    @Test
    fun `with con expression`() {
        val statement = KtStatementExpression.wrapFunction(
                0,
                TYPE_UNIT,
                "con",
                KtExpressionProperty(0, null, "x", null, Symbol(1, 1, 2)),
                listOf(KtUtil.EXPRESSION_NULL),
                FUNCTION_CON
        )
        val block = KtBlock(
                0,
                Symbol(1, 1, 4),
                listOf(),
                listOf(statement)
        )
        val declaration = KtDeclarationPrimaryProperty(
                0,
                "m",
                Symbol(1, 1, 3),
                Symbol(1, 1, 1),
                listOf(KtAnnotationProperty.MAKE),
                KtExpressionOperator(
                        0,
                        Symbol(1, 1, 1),
                        OPERATOR_WITH,
                        KtUtil.EXPRESSION_NULL,
                        listOf(),
                        listOf(block)
                )
        )
        val expected = VkComponentInstance(
                0,
                "m",
                Symbol(1, 1, 3),
                Symbol(1, 1, 1),
                listOf(VkConnection(0, Symbol(1, 1, 2), VkUtil.EXPRESSION_NULL))
        )
        assertEquals(
                expected,
                VkComponentInstance(declaration)
        )
    }

    @Test
    fun `no annotation`() {
        val declaration = KtDeclarationPrimaryProperty(
                0,
                "m",
                Symbol(1, 1, 2),
                null,
                listOf(),
                KtUtil.EXPRESSION_NULL
        )
        assertThrowsMessage<LineException>("component annotation expected") {
            VkComponentInstance(declaration)
        }
    }
}