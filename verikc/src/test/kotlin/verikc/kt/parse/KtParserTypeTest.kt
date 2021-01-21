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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.Symbol
import verikc.kt.KtParseUtil
import verikc.kt.ast.*
import verikc.line

internal class KtParserTypeTest {

    @Test
    fun `type simple`() {
        val string = "class M : Module()"
        val function = KtFunction(
            line(2),
            "t_M",
            Symbol(5),
            listOf(),
            listOf(),
            "M",
            KtBlock(line(2), Symbol(6), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "M",
            Symbol(3),
            false,
            listOf(),
            KtTypeParent(line(2), "Module", listOf()),
            KtProperty(line(2), "M", Symbol(4), MutabilityType.VAL, listOf(), "M", null),
            function,
            null,
            null,
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, KtParseUtil.parseType(string))
    }

    @Test
    fun `type with parameter`() {
        val string = "class M(val x: Int): Module()"
        val function = KtFunction(
            line(2),
            "t_M",
            Symbol(6),
            listOf(),
            listOf(KtProperty(line(2), "x", Symbol(7), MutabilityType.VAL, listOf(), "Int", null)),
            "M",
            KtBlock(line(2), Symbol(8), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "M",
            Symbol(3),
            false,
            listOf(KtProperty(line(2), "x", Symbol(4), MutabilityType.VAL, listOf(), "Int", null)),
            KtTypeParent(line(2), "Module", listOf()),
            KtProperty(line(2), "M", Symbol(5), MutabilityType.VAL, listOf(), "M", null),
            function,
            null,
            null,
            listOf(),
            listOf(),
            listOf()
        )
        assertEquals(expected, KtParseUtil.parseType(string))
    }

    @Test
    fun `type with parameter illegal`() {
        val string = "class M(var x: Int): Module()"
        assertThrowsMessage<LineException>("class parameter cannot be mutable") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with no delegation specifier`() {
        val string = "class M"
        assertThrowsMessage<LineException>("parent type expected") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with multiple delegation specifiers`() {
        val string = "class M: Class(), Module()"
        assertThrowsMessage<LineException>("multiple parent types not permitted") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with enum entries`() {
        val string = """
            enum class Op {
                ADD, SUB
            }
        """.trimIndent()
        val expected = listOf(
            KtProperty(
                line(3),
                "ADD",
                Symbol(9),
                MutabilityType.VAL,
                listOf(),
                null,
                KtExpressionFunction(line(3), "t_Op", null, listOf())
            ),
            KtProperty(
                line(3),
                "SUB",
                Symbol(10),
                MutabilityType.VAL,
                listOf(),
                null,
                KtExpressionFunction(line(3), "t_Op", null, listOf())
            )
        )
        assertEquals(expected, KtParseUtil.parseType(string).enumProperties)
    }

    @Test
    fun `type with declaration`() {
        val string = """
            class M : Module() {
                val x = 0
            }
        """.trimIndent()
        val function = KtFunction(
            line(2),
            "t_M",
            Symbol(5),
            listOf(),
            listOf(),
            "M",
            KtBlock(line(2), Symbol(6), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "M",
            Symbol(3),
            false,
            listOf(),
            KtTypeParent(line(2), "Module", listOf()),
            KtProperty(line(2), "M", Symbol(4), MutabilityType.VAL, listOf(), "M", null),
            function,
            null,
            null,
            listOf(),
            listOf(),
            listOf(
                KtProperty(
                    line(3),
                    "x",
                    Symbol(7),
                    MutabilityType.VAL,
                    listOf(),
                    null,
                    KtExpressionLiteral(line(3), "0")
                )
            )
        )
        assertEquals(expected, KtParseUtil.parseType(string))
    }

    @Test
    fun `type nested`() {
        val string = """
            class C: Class() {
                class D: Class() {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("nested type declaration not permitted") {
            KtParseUtil.parseType(string)
        }
    }
}
