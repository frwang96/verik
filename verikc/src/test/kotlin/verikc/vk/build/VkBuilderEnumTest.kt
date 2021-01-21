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

package verikc.vk.build

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkExpressionLiteral

internal class VkBuilderEnumTest {

    @Test
    fun `enum illegal no properties`() {
        val string = "enum class Op(val value: Ubit)"
        assertThrowsMessage<LineException>("expected enum properties") {
            VkBuildUtil.buildEnum("", string)
        }
    }

    @Test
    fun `get label expressions sequential`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 0, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 2, Line(0)))
        )
        assertEquals(
            expected,
            VkBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_SEQUENTIAL, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions one hot`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(3), LiteralValue.fromBitInt(3, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(3), LiteralValue.fromBitInt(3, 2, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(3), LiteralValue.fromBitInt(3, 4, Line(0)))
        )
        assertEquals(
            expected,
            VkBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_ONE_HOT, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions zero one hot`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 0, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT.toTypeGenerified(2), LiteralValue.fromBitInt(2, 2, Line(0)))
        )
        assertEquals(
            expected,
            VkBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_ZERO_ONE_HOT, 3, Line(0))
        )
    }
}
