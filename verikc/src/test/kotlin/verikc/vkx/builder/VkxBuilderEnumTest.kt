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

package verikc.vkx.builder

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
import verikc.vkx.VkxBuildUtil
import verikc.vkx.ast.VkxEnum
import verikc.vkx.ast.VkxEnumEntry
import verikc.vkx.ast.VkxExpressionLiteral
import verikc.vkx.ast.VkxProperty

internal class VkxBuilderEnumTest {

    @Test
    fun `enum illegal incorrect parameters`() {
        val string = "enum class _op(val value: _int)"
        val message = "enum parameter with identifier value and type _ubit expected"
        assertThrowsMessage<LineException>(message) {
            VkxBuildUtil.buildEnum("", string)
        }
    }

    @Test
    fun `enum illegal no properties`() {
        val string = "enum class _op(val value: _ubit)"
        assertThrowsMessage<LineException>("expected enum properties") {
            VkxBuildUtil.buildEnum("", string)
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
            VkxEnumEntry(
                VkxProperty(
                    line(4),
                    "ADD",
                    Symbol(7),
                    Symbol(3).toTypeGenerifiedInstance()
                ),
                VkxExpressionLiteral(
                    line(4),
                    TYPE_UBIT.toTypeGenerifiedInstance(1),
                    LiteralValue.fromBitInt(1, 0, line(4))
                )
            ),
            VkxEnumEntry(
                VkxProperty(
                    line(4),
                    "SUB",
                    Symbol(8),
                    Symbol(3).toTypeGenerifiedInstance()
                ),
                VkxExpressionLiteral(
                    line(4),
                    TYPE_UBIT.toTypeGenerifiedInstance(1),
                    LiteralValue.fromBitInt(1, 1, line(4))
                )
            )
        )
        val expected = VkxEnum(
            line(3),
            "_op",
            Symbol(3),
            Symbol(5),
            enumEntries,
            1
        )
        assertEquals(expected, VkxBuildUtil.buildEnum("", string))
    }

    @Test
    fun `enum automatic labels`() {
        val string = """
            enum class _op(val value: _ubit = enum_sequential()) {
                ADD, SUB
            }
        """.trimIndent()
        val enumEntries = listOf(
            VkxEnumEntry(
                VkxProperty(
                    line(4),
                    "ADD",
                    Symbol(7),
                    Symbol(3).toTypeGenerifiedInstance()
                ),
                VkxExpressionLiteral(
                    line(3),
                    TYPE_UBIT.toTypeGenerifiedInstance(1),
                    LiteralValue.fromBitInt(1, 0, line(3))
                )
            ),
            VkxEnumEntry(
                VkxProperty(
                    line(4),
                    "SUB",
                    Symbol(8),
                    Symbol(3).toTypeGenerifiedInstance()
                ),
                VkxExpressionLiteral(
                    line(3),
                    TYPE_UBIT.toTypeGenerifiedInstance(1),
                    LiteralValue.fromBitInt(1, 1, line(3))
                )
            )
        )
        val expected = VkxEnum(
            line(3),
            "_op",
            Symbol(3),
            Symbol(5),
            enumEntries,
            1
        )
        assertEquals(expected, VkxBuildUtil.buildEnum("", string))
    }

    @Test
    fun `get label expressions sequential`() {
        val expected = listOf(
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 0, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 1, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 2, Line(0))
            )
        )
        assertEquals(
            expected,
            VkxBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_SEQUENTIAL, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions one hot`() {
        val expected = listOf(
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(3),
                LiteralValue.fromBitInt(3, 1, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(3),
                LiteralValue.fromBitInt(3, 2, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(3),
                LiteralValue.fromBitInt(3, 4, Line(0))
            )
        )
        assertEquals(
            expected,
            VkxBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_ONE_HOT, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions zero one hot`() {
        val expected = listOf(
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 0, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 1, Line(0))
            ),
            VkxExpressionLiteral(
                Line(0),
                TYPE_UBIT.toTypeGenerifiedInstance(2),
                LiteralValue.fromBitInt(2, 2, Line(0))
            )
        )
        assertEquals(
            expected,
            VkxBuilderEnum.getExpressionsWithLabelingFunction(FUNCTION_ENUM_ZERO_ONE_HOT, 3, Line(0))
        )
    }
}
