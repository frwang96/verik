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
        val string = "class _m: _module()"
        val function = KtFunction(
            line(2),
            "_m",
            Symbol(5),
            listOf(),
            listOf(),
            "_m",
            KtBlock(line(2), Symbol(6), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "_m",
            Symbol(3),
            false,
            listOf(),
            listOf(),
            KtTypeParent(line(2), "_module", listOf()),
            KtProperty(line(2), "_m", Symbol(4), MutabilityType.VAL, listOf(), "_m", null),
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
        val string = "class _m(val x: _int): _module()"
        val function = KtFunction(
            line(2),
            "_m",
            Symbol(6),
            listOf(),
            listOf(KtProperty(line(2), "x", Symbol(7), MutabilityType.VAL, listOf(), "_int", null)),
            "_m",
            KtBlock(line(2), Symbol(8), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "_m",
            Symbol(3),
            false,
            listOf(),
            listOf(KtProperty(line(2), "x", Symbol(4), MutabilityType.VAL, listOf(), "_int", null)),
            KtTypeParent(line(2), "_module", listOf()),
            KtProperty(line(2), "_m", Symbol(5), MutabilityType.VAL, listOf(), "_m", null),
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
        val string = "class _m(var x: _int): _module()"
        assertThrowsMessage<LineException>("class parameter cannot be mutable") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with no delegation specifier`() {
        val string = "class _m"
        assertThrowsMessage<LineException>("parent type expected") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with multiple delegation specifiers`() {
        val string = "class _m: _class(), _module()"
        assertThrowsMessage<LineException>("multiple parent types not permitted") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with enum entries`() {
        val string = """
            enum class _op(val value: _ubit) {
                ADD, SUB
            }
        """.trimIndent()
        val expected = listOf(
            KtProperty(
                line(3),
                "ADD",
                Symbol(11),
                MutabilityType.VAL,
                listOf(),
                null,
                KtExpressionFunction(line(3), "_op", null, listOf())
            ),
            KtProperty(
                line(3),
                "SUB",
                Symbol(12),
                MutabilityType.VAL,
                listOf(),
                null,
                KtExpressionFunction(line(3), "_op", null, listOf())
            )
        )
        assertEquals(expected, KtParseUtil.parseType(string).enumProperties)
    }

    @Test
    fun `type with enum entries incorrect parameters`() {
        val string = """
            enum class _x(val value: _int) {
                ADD, SUB
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("enum constructor function does not have the appropriate parameters") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type with declaration`() {
        val string = """
            class _m: _module() {
                val x = 0
            }
        """.trimIndent()
        val function = KtFunction(
            line(2),
            "_m",
            Symbol(5),
            listOf(),
            listOf(),
            "_m",
            KtBlock(line(2), Symbol(6), listOf(), listOf())
        )
        val expected = KtType(
            line(2),
            "_m",
            Symbol(3),
            false,
            listOf(),
            listOf(),
            KtTypeParent(line(2), "_module", listOf()),
            KtProperty(line(2), "_m", Symbol(4), MutabilityType.VAL, listOf(), "_m", null),
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
            class _x: _class() {
                class _y: _class() {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("nested type declaration not permitted") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type illegal name no underscore`() {
        val string = "class m: _module()"
        assertThrowsMessage<LineException>("type identifier should begin with a single underscore") {
            KtParseUtil.parseType(string)
        }
    }

    @Test
    fun `type illegal name reserved`() {
        val string = "class _always: _module()"
        assertThrowsMessage<LineException>("identifier always is reserved in SystemVerilog") {
            KtParseUtil.parseType(string)
        }
    }
}
