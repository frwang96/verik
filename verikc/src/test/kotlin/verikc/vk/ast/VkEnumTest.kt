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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.KtUtil
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT

internal class VkEnumTest {

    @Test
    fun `enum illegal`() {
        val string = "class _op(override val value: _ubit = enum_sequential()): _enum"
        val declaration = KtUtil.resolveDeclaration(string)
        assertThrowsMessage<LineException>("expected enum properties") {
            VkEnum(declaration)
        }
    }

    @Test
    @Disabled
    fun `enum manually labeled`() {
        val string = """
            enum class _op(override val value: _ubit): _enum {
                ADD(ubit(0)), SUB(ubit(1))
            }
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string)
        val enumEntries = listOf(
            VkEnumProperty(
                Line(2),
                "ADD",
                Symbol(7),
                Symbol(3),
                VkExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))
            ),
            VkEnumProperty(
                Line(2),
                "SUB",
                Symbol(8),
                Symbol(3),
                VkExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(1))
            )
        )
        val expected = VkEnum(
            Line(1),
            "_op",
            Symbol(3),
            Symbol(5),
            enumEntries,
            31
        )
        assertEquals(expected, VkEnum(declaration))
    }

    @Test
    fun `enum automatically labeled`() {
        val string = """
            enum class _op(override val value: _ubit = enum_sequential()): _enum {
                ADD, SUB
            }
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string)
        val enumEntries = listOf(
            VkEnumProperty(
                Line(2),
                "ADD",
                Symbol(7),
                Symbol(3),
                VkExpressionLiteral(Line(1), TYPE_UBIT, LiteralValue.fromBitInt(1, 0, Line(1)))
            ),
            VkEnumProperty(
                Line(2),
                "SUB",
                Symbol(8),
                Symbol(3),
                VkExpressionLiteral(Line(1), TYPE_UBIT, LiteralValue.fromBitInt(1, 1, Line(1)))
            )
        )
        val expected = VkEnum(
            Line(1),
            "_op",
            Symbol(3),
            Symbol(5),
            enumEntries,
            1
        )
        assertEquals(expected, VkEnum(declaration))
    }

    @Test
    fun `get label expressions sequential`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 0, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 2, Line(0)))
        )
        assertEquals(
            expected,
            VkEnum.getLabelExpressions(FUNCTION_ENUM_SEQUENTIAL, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions one hot`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(3, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(3, 2, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(3, 4, Line(0)))
        )
        assertEquals(
            expected,
            VkEnum.getLabelExpressions(FUNCTION_ENUM_ONE_HOT, 3, Line(0))
        )
    }

    @Test
    fun `get label expressions zero one hot`() {
        val expected = listOf(
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 0, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 1, Line(0))),
            VkExpressionLiteral(Line(0), TYPE_UBIT, LiteralValue.fromBitInt(2, 2, Line(0)))
        )
        assertEquals(
            expected,
            VkEnum.getLabelExpressions(FUNCTION_ENUM_ZERO_ONE_HOT, 3, Line(0))
        )
    }
}
