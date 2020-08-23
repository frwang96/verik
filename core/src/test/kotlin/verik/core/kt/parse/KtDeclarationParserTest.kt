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

package verik.core.kt.parse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.TYPE_INT

internal class KtDeclarationParserTest {

    @Test
    fun `annotation on property`() {
        val string = "@wire val x = 0"
        val declaration = KtUtil.parseDeclaration(string) as KtDeclarationBaseProperty
        assertEquals(listOf(KtAnnotationProperty.WIRE), declaration.annotations)
    }

    @Test
    fun `annotation on property not supported`() {
        val string = "@x val x = 0"
        assertThrowsMessage<LineException>("annotation x not supported for property declaration") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `type simple`() {
        val string = "class x: _class"
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(1, "_class", listOf(), null),
                null,
                listOf()
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `type with parameters`() {
        val string = "class x(val x: Int): _class"
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(KtDeclarationParameter(
                        1,
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        "Int",
                        null
                )),
                KtConstructorInvocation(1, "_class", listOf(), null),
                null,
                listOf()
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `type with no delegation specifier`() {
        val string = "class x"
        assertThrowsMessage<LineException>("parent type expected") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `type with multiple delegation specifiers`() {
        val string = "class x: _class, _interf"
        assertThrowsMessage<LineException>("multiple parent types not permitted") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `type with enum entries`() {
        val string = """
            enum class x: _enum {
                ADD, SUB
            }
        """.trimIndent()
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(1, "_enum", listOf(), null),
                listOf(
                        KtDeclarationEnumEntry(2, "ADD", Symbol(1, 1, 2), null, null),
                        KtDeclarationEnumEntry(2, "SUB", Symbol(1, 1, 3), null, null)
                ),
                listOf()
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `type with declaration`() {
        val string = """
            class x: _class {
                val x = 0
            }
        """.trimIndent()
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(1, "_class", listOf(), null),
                null,
                listOf(KtDeclarationBaseProperty(
                        2,
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        listOf(),
                        KtExpressionLiteral(2, TYPE_INT, LiteralValue.fromIntImplicit(0))
                ))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `type nested`() {
        val string = """
            class x: _class {
                class y: _class {}
            }
        """.trimIndent()
        assertThrowsMessage<LineException>("nested class declaration not permitted") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `function simple`() {
        val string = "fun x() {}"
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Unit",
                KtBlock(1, listOf()),
                null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `function with parameters`() {
        val string = "fun x(x: Int) {}"
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(KtDeclarationParameter(
                        1,
                        "x",
                        Symbol(1, 1, 2),
                        null,
                        "Int",
                        null
                )),
                "Unit",
                KtBlock(1, listOf()),
                null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `function with return type`() {
        val string = "fun x(): Int {}"
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Int",
                KtBlock(1, listOf()),
                null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `function with block`() {
        val string = "fun x() { 0 }"
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Unit",
                KtBlock(1, listOf(KtStatementExpression(
                        1,
                        KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0))
                ))),
                null
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }

    @Test
    fun `function expression`() {
        val string = "fun x() = 0"
        assertThrowsMessage<LineException>("function expressions are not supported") {
            KtUtil.parseDeclaration(string)
        }
    }

    @Test
    fun `property simple`() {
        val string = "val x = 0"
        val expected = KtDeclarationBaseProperty(
                1,
                "x",
                Symbol(1, 1, 1),
                null,
                listOf(),
                KtExpressionLiteral(1, TYPE_INT, LiteralValue.fromIntImplicit(0))
        )
        assertEquals(expected, KtUtil.parseDeclaration(string))
    }
}