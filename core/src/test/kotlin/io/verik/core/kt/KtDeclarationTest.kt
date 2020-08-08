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

package io.verik.core.kt

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assert.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtDeclarationTest {

    @Test
    fun `modifier on property`() {
        val rule = AlRuleParser.parseDeclaration("public val x = 0")
        val declaration = KtDeclaration(rule)
        assertEquals(listOf<KtModifier>(), declaration.modifiers)
    }

    @Test
    fun `annotation on property`() {
        val rule = AlRuleParser.parseDeclaration("@rand val x = 0")
        val declaration = KtDeclaration(rule)
        assertEquals(listOf(KtModifier.RAND), declaration.modifiers)
    }

    @Test
    fun `annotation on property not supported`() {
        val rule = AlRuleParser.parseDeclaration("@x val x = 0")
        assertThrowsMessage<FileLineException>("annotation x not supported") {
            KtDeclaration(rule)
        }
    }

    @Test
    fun `class simple`() {
        val rule = AlRuleParser.parseDeclaration("class x: _class")
        val expected = KtDeclarationClass(
                "x",
                listOf(),
                FileLine(1),
                listOf(),
                KtDelegationSpecifier("_class", listOf(), FileLine(1)),
                null,
                listOf()
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `class with parameters`() {
        val rule = AlRuleParser.parseDeclaration("class x(val x: Int): _class")
        val expected = KtDeclarationClass(
                "x",
                listOf(),
                FileLine(1),
                listOf(KtParameter("x", "Int", null, FileLine(1), null)),
                KtDelegationSpecifier("_class", listOf(), FileLine(1)),
                null,
                listOf()
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `class with no delegation specifier`() {
        val rule = AlRuleParser.parseDeclaration("class x")
        assertThrowsMessage<FileLineException>("delegation specifier expected") {
            KtDeclaration(rule)
        }
    }

    @Test
    fun `class with multiple delegation specifiers`() {
        val rule = AlRuleParser.parseDeclaration("class x: _class, _interf")
        assertThrowsMessage<FileLineException>("multiple delegation specifiers not supported") {
            KtDeclaration(rule)
        }
    }

    @Test
    fun `class with enum entries`() {
        val rule = AlRuleParser.parseDeclaration("""
            enum class x: _enum {
                ADD, SUB
            }
        """.trimIndent())
        val expected = KtDeclarationClass(
                "x",
                listOf(KtModifier.ENUM),
                FileLine(1),
                listOf(),
                KtDelegationSpecifier("_enum", listOf(), FileLine(1)),
                listOf(
                        KtEnumEntry("ADD", listOf(), FileLine(2)),
                        KtEnumEntry("SUB", listOf(), FileLine(2))
                ),
                listOf()
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `class with declaration`() {
        val rule = AlRuleParser.parseDeclaration("""
            class x: _class {
                val x = 0
            }
        """.trimIndent())
        val expected = KtDeclarationClass(
                "x",
                listOf(),
                FileLine(1),
                listOf(),
                KtDelegationSpecifier("_class", listOf(), FileLine(1)),
                null,
                listOf(
                        KtDeclarationProperty(
                                "x",
                                listOf(),
                                FileLine(2),
                                KtExpressionLiteral(FileLine(2), "0"),
                                null
                        )
                )
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `function simple`() {
        val rule = AlRuleParser.parseDeclaration("fun x() {}")
        val expected = KtDeclarationFunction(
                "x",
                listOf(),
                FileLine(1),
                listOf(),
                null,
                KtBlock(listOf(), FileLine(1)),
                null
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `function with parameters`() {
        val rule = AlRuleParser.parseDeclaration("fun x(x: Int) {}")
        val expected = KtDeclarationFunction(
                "x",
                listOf(),
                FileLine(1),
                listOf(KtParameter("x", "Int", null, FileLine(1), null)),
                null,
                KtBlock(listOf(), FileLine(1)),
                null
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `function with return type`() {
        val rule = AlRuleParser.parseDeclaration("fun x(): Int {}")
        val expected = KtDeclarationFunction(
                "x",
                listOf(),
                FileLine(1),
                listOf(),
                "Int",
                KtBlock(listOf(), FileLine(1)),
                null
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `function with block`() {
        val rule = AlRuleParser.parseDeclaration("fun x() { 0 }")
        val expected = KtDeclarationFunction(
                "x",
                listOf(),
                FileLine(1),
                listOf(),
                null,
                KtBlock(listOf(KtStatement(KtExpressionLiteral(FileLine(1), "0"), FileLine(1))), FileLine(1)),
                null
        )
        assertEquals(expected, KtDeclaration(rule))
    }

    @Test
    fun `function expression`() {
        val rule = AlRuleParser.parseDeclaration("fun x() = 0")
        assertThrowsMessage<FileLineException>("function expressions are not supported") {
            KtDeclaration(rule)
        }
    }

    @Test
    fun `property simple`() {
        val rule = AlRuleParser.parseDeclaration("val x = 0")
        val expected = KtDeclarationProperty(
                "x",
                listOf(),
                FileLine(1),
                KtExpressionLiteral(FileLine(1), "0"),
                null
        )
        assertEquals(expected, KtDeclaration(rule))
    }
}