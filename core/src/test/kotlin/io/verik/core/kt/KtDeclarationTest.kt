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

import io.verik.core.main.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import io.verik.core.kt.resolve.KtSymbolIndexer
import io.verik.core.kt.resolve.KtSymbolMap
import io.verik.core.main.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtDeclarationTest {

    @Test
    fun `annotation on property`() {
        val rule = AlRuleParser.parseDeclaration("@rand val x = 0")
        val declaration = KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))) as KtDeclarationProperty
        assertEquals(listOf(KtAnnotationProperty.RAND), declaration.annotations)
    }

    @Test
    fun `annotation on property not supported`() {
        val rule = AlRuleParser.parseDeclaration("@x val x = 0")
        assertThrowsMessage<LineException>("annotation x not supported for property declaration") {
            KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        }
    }

    @Test
    fun `type simple`() {
        val rule = AlRuleParser.parseDeclaration("class x: _class")
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
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `type with parameters`() {
        val rule = AlRuleParser.parseDeclaration("class x(val x: Int): _class")
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 2),
                listOf(),
                listOf(KtDeclarationParameter(1, "x", Symbol(1, 1, 1), "Int", null, null)),
                KtConstructorInvocation(1, "_class", listOf(), null),
                null,
                listOf()
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `type with no delegation specifier`() {
        val rule = AlRuleParser.parseDeclaration("class x")
        assertThrowsMessage<LineException>("parent type expected") {
            KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        }
    }

    @Test
    fun `type with multiple delegation specifiers`() {
        val rule = AlRuleParser.parseDeclaration("class x: _class, _interf")
        assertThrowsMessage<LineException>("multiple parent types not permitted") {
            KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        }
    }

    @Test
    fun `type with enum entries`() {
        val rule = AlRuleParser.parseDeclaration("""
            enum class x: _enum {
                ADD, SUB
            }
        """.trimIndent())
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 3),
                listOf(),
                listOf(),
                KtConstructorInvocation(1, "_enum", listOf(), null),
                listOf(
                        KtDeclarationEnumEntry(2, "ADD", Symbol(1, 1, 1), null, null),
                        KtDeclarationEnumEntry(2, "SUB", Symbol(1, 1, 2), null, null)
                ),
                listOf()
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `type with declaration`() {
        val rule = AlRuleParser.parseDeclaration("""
            class x: _class {
                val x = 0
            }
        """.trimIndent())
        val expected = KtDeclarationType(
                1,
                "x",
                Symbol(1, 1, 2),
                listOf(),
                listOf(),
                KtConstructorInvocation(1, "_class", listOf(), null),
                null,
                listOf(KtDeclarationProperty(2, "x", Symbol(1, 1, 1), listOf(), KtExpressionLiteral(2, "0")))
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `type nested`() {
        val rule = AlRuleParser.parseDeclaration("""
            class x: _class {
                class y: _class {}
            }
        """.trimIndent())
        assertThrowsMessage<LineException>("nested class declaration not permitted") {
            KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        }
    }

    @Test
    fun `function simple`() {
        val rule = AlRuleParser.parseDeclaration("fun x() {}")
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
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `function with parameters`() {
        val rule = AlRuleParser.parseDeclaration("fun x(x: Int) {}")
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 2),
                listOf(),
                listOf(KtDeclarationParameter(1, "x", Symbol(1, 1, 1), "Int", null, null)),
                "Unit",
                KtBlock(1, listOf()),
                null
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `function with return type`() {
        val rule = AlRuleParser.parseDeclaration("fun x(): Int {}")
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
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `function with block`() {
        val rule = AlRuleParser.parseDeclaration("fun x() { 0 }")
        val expected = KtDeclarationFunction(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Unit",
                KtBlock(1, listOf(KtStatement(1, KtExpressionLiteral(1, "0")))),
                null
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }

    @Test
    fun `function expression`() {
        val rule = AlRuleParser.parseDeclaration("fun x() = 0")
        assertThrowsMessage<LineException>("function expressions are not supported") {
            KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        }
    }

    @Test
    fun `property simple`() {
        val rule = AlRuleParser.parseDeclaration("val x = 0")
        val expected = KtDeclarationProperty(
                1,
                "x",
                Symbol(1, 1, 1),
                listOf(),
                KtExpressionLiteral(1, "0")
        )
        assertEquals(expected, KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1))))
    }
}