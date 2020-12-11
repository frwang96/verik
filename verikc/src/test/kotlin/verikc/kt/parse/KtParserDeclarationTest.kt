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
                Symbol(1, 1, 2),
                listOf(),
                Symbol(1, 1, 1)
        )
        val expected = KtPrimaryType(
                Line(1),
                "_x",
                Symbol(1, 1, 1),
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
        val string = "class _x(val x: Int): _class"
        val constructorFunction = KtConstructorFunction(
                Line(1),
                "_x",
                Symbol(1, 1, 3),
                listOf(KtParameterProperty(
                        Line(1),
                        "x",
                        Symbol(1, 1, 4),
                        null,
                        "Int",
                        null
                )),
                Symbol(1, 1, 1)
        )
        val expected = KtPrimaryType(
                Line(1),
                "_x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(KtParameterProperty(
                        Line(1),
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        "Int",
                        null
                )),
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
                Symbol(1, 1, 3),
                listOf(),
                Symbol(1, 1, 1)
        )
        val objectType = KtObjectType(
                Line(1),
                "_x",
                Symbol(1, 1, 4),
                listOf(),
                listOf(
                        KtEnumProperty(Line(2), "ADD", Symbol(1, 1, 5), Symbol(1, 1, 1), null),
                        KtEnumProperty(Line(2), "SUB", Symbol(1, 1, 6), Symbol(1, 1, 1), null)
                ),
                KtObjectProperty(Line(1), "_x", Symbol(1, 1, 7), Symbol(1, 1, 4))
        )
        val expected = KtPrimaryType(
                Line(1),
                "_x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                listOf(KtParameterProperty(Line(1), "value", Symbol(1, 1, 2), null, "_int", null)),
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
                Symbol(1, 1, 3),
                listOf(),
                Symbol(1, 1, 1)
        )
        val expected = KtPrimaryType(
                Line(1),
                "_x",
                Symbol(1, 1, 1),
                listOf(KtPrimaryProperty(
                        Line(2),
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        listOf(),
                        KtExpressionLiteral(Line(2), TYPE_INT, LiteralValue.fromInt(0))
                )),
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
                Symbol(1, 1, 1),
                listOf(),
                null,
                listOf(),
                KtFunctionBodyBlock(
                        "Unit",
                        KtBlock(Line(1), Symbol(1, 1, 2), listOf(), listOf())
                )
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with parameters`() {
        val string = "fun x(x: Int) {}"
        val expected = KtPrimaryFunction(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                listOf(KtParameterProperty(
                        Line(1),
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        "Int",
                        null
                )),
                null,
                listOf(),
                KtFunctionBodyBlock(
                        "Unit",
                        KtBlock(Line(1), Symbol(1, 1, 3), listOf(), listOf())
                )
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with return type`() {
        val string = "fun x(): Int {}"
        val expected = KtPrimaryFunction(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                listOf(),
                null,
                listOf(),
                KtFunctionBodyBlock(
                        "Int",
                        KtBlock(Line(1), Symbol(1, 1, 2), listOf(), listOf())
                )
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with block`() {
        val string = "fun x() { 0 }"
        val expected = KtPrimaryFunction(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                listOf(),
                null,
                listOf(),
                KtFunctionBodyBlock("Unit", KtBlock(
                        Line(1),
                        Symbol(1, 1, 2),
                        listOf(),
                        listOf(KtStatementExpression.wrapLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0)))
                ))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary function with expression`() {
        val string = "fun x() = 0"
        val expected = KtPrimaryFunction(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                listOf(),
                null,
                listOf(),
                KtFunctionBodyExpression(KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0)))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `primary property simple`() {
        val string = "val x = 0"
        val expected = KtPrimaryProperty(
                Line(1),
                "x",
                Symbol(1, 1, 1),
                null,
                listOf(),
                KtExpressionLiteral(Line(1), TYPE_INT, LiteralValue.fromInt(0))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }
}
