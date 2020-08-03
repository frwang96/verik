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

package io.verik.core

import io.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HeaderParserTest {

    @Test
    fun `empty file`() {
        val rule = KtRuleParser.parseKotlinFile("")
        assertEquals(listOf<HeaderDeclaration>(), HeaderParser.parse(rule))
    }

    @Test
    fun `interface header`() {
        val rule = KtRuleParser.parseKotlinFile("""
            class _x: _interf {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclarationInterf("x", listOf())), HeaderParser.parse(rule))
    }

    @Test
    fun `interface header with modport`() {
        val rule = KtRuleParser.parseKotlinFile("""
            class _x: _interf {
                class _y: _modport {}
            }
        """.trimIndent())
        assertEquals(listOf(HeaderDeclarationInterf("x", listOf("y"))), HeaderParser.parse(rule))
    }

    @Test
    fun `class header`() {
        val rule = KtRuleParser.parseKotlinFile("""
            class _x: _class {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclarationClass("x", true)), HeaderParser.parse(rule))
    }

    @Test
    fun `subclass header`() {
        val rule = KtRuleParser.parseKotlinFile("""
            class _y: _x {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclarationClass("y", false)), HeaderParser.parse(rule))
    }

    @Test
    fun `enum header`() {
        val rule = KtRuleParser.parseKotlinFile("""
            enum class _op(val value: _uint = _enum.SEQUENTIAL): _enum {
                ADD, SUB
            }
        """.trimIndent())
        assertEquals(listOf(HeaderDeclarationEnum("op")), HeaderParser.parse(rule))
    }
}