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

package verikc.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.KtUtil
import verikc.kt.ast.*
import verikc.lang.LangSymbol.TYPE_INT
import verikc.vk.VkUtil

internal class KtParserDeclarationTest {

    @Test
    fun `primary property annotation`() {
        val string = "@input val x = 0"
        val declaration = KtUtil.parseDeclaration(string) as KtPrimaryProperty
        assertEquals(listOf(KtAnnotationProperty.INPUT), declaration.annotations)
    }

    @Test
    fun `primary property annotation not supported`() {
        val string = "@x val x = 0"
        assertThrowsMessage<LineException>("annotation x not supported for property declaration") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `primary type simple`() {
        val string = "class _x: _class"
        val constructorFunction = KtConstructorFunction(
            Line(1),
            "_x",
            Symbol(4),
            listOf(),
            Symbol(3)
        )
        val expected = KtPrimaryType(
            Line(1),
            "_x",
            Symbol(3),
            listOf(),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(1), "_class", listOf(), null),
            constructorFunction,
            null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary type with parameters`() {
        val string = "class _x(val x: _int): _class"
        val constructorFunction = KtConstructorFunction(
            Line(1),
            "_x",
            Symbol(5),
            listOf(KtParameterProperty(Line(1), "x", Symbol(6), null, "_int", null)),
            Symbol(3)
        )
        val expected = KtPrimaryType(
            Line(1),
            "_x",
            Symbol(3),
            listOf(),
            listOf(),
            listOf(KtParameterProperty(Line(1), "x", Symbol(4), null, "_int", null)),
            KtConstructorInvocation(Line(1), "_class", listOf(), null),
            constructorFunction,
            null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary type with no delegation specifier`() {
        val string = "class _x"
        assertThrowsMessage<LineException>("parent type expected") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `primary type with multiple delegation specifiers`() {
        val string = "class _x: _class, _interf"
        assertThrowsMessage<LineException>("multiple parent types not permitted") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `primary type with enum entries`() {
        val string = """
            enum class _x(override val value: _int): _enum {
                ADD, SUB
            }
        """.trimIndent()
        val constructorFunction = KtConstructorFunction(
            Line(1),
            "_x",
            Symbol(5),
            listOf(),
            Symbol(3)
        )
        val objectType = KtObjectType(
            Line(1),
            "_x",
            Symbol(6),
            listOf(),
            listOf(
                KtEnumProperty(Line(2), "ADD", Symbol(7), Symbol(3), null),
                KtEnumProperty(Line(2), "SUB", Symbol(8), Symbol(3), null)
            ),
            KtObjectProperty(Line(1), "_x", Symbol(9), Symbol(6))
        )
        val expected = KtPrimaryType(
            Line(1),
            "_x",
            Symbol(3),
            listOf(),
            listOf(),
            listOf(KtParameterProperty(Line(1), "value", Symbol(4), null, "_int", null)),
            KtConstructorInvocation(Line(1), "_enum", listOf(), null),
            constructorFunction,
            objectType
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary type with declaration`() {
        val string = """
            class _x: _class {
                val x = 0
            }
        """.trimIndent()
        val constructorFunction = KtConstructorFunction(
            Line(1),
            "_x",
            Symbol(5),
            listOf(),
            Symbol(3)
        )
        val expected = KtPrimaryType(
            Line(1),
            "_x",
            Symbol(3),
            listOf(
                KtPrimaryProperty(
                    Line(2),
                    "x",
                    Symbol(4),
                    null,
                    listOf(),
                    KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))
                )
            ),
            listOf(),
            listOf(),
            KtConstructorInvocation(Line(1), "_class", listOf(), null),
            constructorFunction,
            null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary type nested`() {
        val string = """
            class _x: _class {
                class _y: _class {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("nested type declaration not permitted") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `primary type illegal name`() {
        val string = "class m: _module"
        assertThrowsMessage<LineException>("type identifier should begin with a single underscore") {
            VkUtil.parseModule(string)
        }
    }

    @Test
    fun `primary function simple`() {
        val string = "fun x() {}"
        val expected = KtPrimaryFunction(
            Line(1),
            "x",
            Symbol(3),
            listOf(),
            null,
            listOf(),
            "_unit",
            KtBlock(Line(1), Symbol(4), listOf(), listOf())
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with parameters`() {
        val string = "fun x(x: _int) {}"
        val expected = KtPrimaryFunction(
            Line(1),
            "x",
            Symbol(3),
            listOf(KtParameterProperty(Line(1), "x", Symbol(4), null, "_int", null)),
            null,
            listOf(),
            "_unit",
            KtBlock(Line(1), Symbol(5), listOf(), listOf())
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with return type`() {
        val string = "fun x(): _int {}"
        val expected = KtPrimaryFunction(
            Line(1),
            "x",
            Symbol(3),
            listOf(),
            null,
            listOf(),
            "_int",
            KtBlock(Line(1), Symbol(4), listOf(), listOf())
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with block`() {
        val string = "fun x() { 0 }"
        val expected = KtPrimaryFunction(
            Line(1),
            "x",
            Symbol(3),
            listOf(),
            null,
            listOf(),
            "_unit",
            KtBlock(
                Line(1),
                Symbol(4),
                listOf(),
                listOf(KtStatementExpression.wrapLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0)))
            )
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary property simple`() {
        val string = "val x = 0"
        val expected = KtPrimaryProperty(
            Line(1),
            "x",
            Symbol(3),
            null,
            listOf(),
            KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }
}
