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
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.TYPE_INT

internal class VkEnumTest {

    @Test
    fun `enum illegal`() {
        val string = "class _e(val value: _int): _enum"
        val declaration = KtUtil.resolveDeclaration(string)
        assertThrowsMessage<LineException>("expected enum entries") {
            VkEnum(declaration)
        }
    }

    @Test
    fun `enum manually labeled`() {
        val string = """
            enum class _op(override val value: _int): _enum {
                ADD(0), SUB(1)
            }
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string)
        val enumEntries = listOf(
                VkEnumEntry(2, "ADD", Symbol(1, 1, 5), VkExpressionLiteral(2, TYPE_INT, LiteralValue.fromInt(0))),
                VkEnumEntry(2, "SUB", Symbol(1, 1, 6), VkExpressionLiteral(2, TYPE_INT, LiteralValue.fromInt(1)))
        )
        val expected = VkEnum(
                1,
                "_op",
                Symbol(1, 1, 1),
                enumEntries,
                1
        )
        assertEquals(expected, VkEnum(declaration))
    }

    @Test
    fun `enum automatically labeled`() {
        val string = """
            enum class _op(override val value: _int = enum_sequential()): _enum {
                ADD, SUB
            }
        """.trimIndent()
        val declaration = KtUtil.resolveDeclaration(string)
        val enumEntries = listOf(
                VkEnumEntry(2, "ADD", Symbol(1, 1, 5), VkExpressionLiteral(2, TYPE_INT, LiteralValue.fromInt(0))),
                VkEnumEntry(2, "SUB", Symbol(1, 1, 6), VkExpressionLiteral(2, TYPE_INT, LiteralValue.fromInt(1)))
        )
        val expected = VkEnum(
                1,
                "_op",
                Symbol(1, 1, 1),
                enumEntries,
                1
        )
        assertEquals(expected, VkEnum(declaration))
    }
}