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
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkEnum
import verikc.vk.ast.VkEnumEntry
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkProperty

internal class VkBuilderEnumTest {

    @Test
    fun `enum illegal no properties`() {
        val string = "enum class _op(val value: _ubit)"
        assertThrowsMessage<LineException>("expected enum properties") {
            VkBuildUtil.buildEnum("", string)
        }
    }

    @Test
    fun `enum manual labels`() {
        val string = """
            enum class _op(val value: _ubit) {
                ADD(ubit(0)), SUB(ubit(1))
            }
        """.trimIndent()
        val enumEntries = listOf(
            VkEnumEntry(
                VkProperty(line(4), "ADD", Symbol(11), Symbol(3).toTypeGenerified()),
                VkExpressionLiteral(line(4), TYPE_UBIT.toTypeGenerified(1), LiteralValue.fromBitInt(1, 0, line(4)))),
            VkEnumEntry(
                VkProperty(line(4), "SUB", Symbol(12), Symbol(3).toTypeGenerified()),
                VkExpressionLiteral(line(4), TYPE_UBIT.toTypeGenerified(1), LiteralValue.fromBitInt(1, 1, line(4))))
        )
        val expected = VkEnum(
            line(3),
            "_op",
            Symbol(3),
            Symbol(6),
            enumEntries,
            1
        )
        assertEquals(expected, VkBuildUtil.buildEnum("", string))
    }

    @Test
    fun `enum automatic labels`() {
        val string = """
            enum class _op(val value: _ubit = enum_sequential()) {
                ADD, SUB
            }
        """.trimIndent()
        val enumEntries = listOf(
            VkEnumEntry(
                VkProperty(line(4), "ADD", Symbol(11), Symbol(3).toTypeGenerified()),
                VkExpressionLiteral(line(3), TYPE_UBIT.toTypeGenerified(1), LiteralValue.fromBitInt(1, 0, line(3)))),
            VkEnumEntry(
                VkProperty(line(4), "SUB", Symbol(12), Symbol(3).toTypeGenerified()),
                VkExpressionLiteral(line(3), TYPE_UBIT.toTypeGenerified(1), LiteralValue.fromBitInt(1, 1, line(3))))
        )
        val expected = VkEnum(
            line(3),
            "_op",
            Symbol(3),
            Symbol(6),
            enumEntries,
            1
        )
        assertEquals(expected, VkBuildUtil.buildEnum("", string))
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
