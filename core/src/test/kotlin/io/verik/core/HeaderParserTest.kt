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

import io.verik.core.al.AlRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HeaderParserTest {

    @Test
    fun `empty file`() {
        val rule = AlRuleParser.parseKotlinFile("")
        assertEquals(listOf<HeaderDeclaration>(), HeaderParser.parse(rule))
    }

    @Test
    fun `module header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            class _x: _module {}
        """.trimIndent())
        assertEquals(listOf<HeaderDeclaration>(), HeaderParser.parse(rule))
    }

    @Test
    fun `interface header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            class _x: _interf {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclaration("_x", HeaderDeclarationType.INTERF)), HeaderParser.parse(rule))
    }

    @Test
    fun `modport header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            class _x: _modport {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclaration("_x", HeaderDeclarationType.MODPORT)), HeaderParser.parse(rule))
    }

    @Test
    fun `class header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            class _x: _class {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclaration("_x", HeaderDeclarationType.CLASS)), HeaderParser.parse(rule))
    }

    @Test
    fun `subclass header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            class _y: _x {}
        """.trimIndent())
        assertEquals(listOf(HeaderDeclaration("_y", HeaderDeclarationType.CLASS_CHILD)), HeaderParser.parse(rule))
    }

    @Test
    fun `enum header`() {
        val rule = AlRuleParser.parseKotlinFile("""
            enum class _op(val value: _uint = _enum.SEQUENTIAL): _enum {
                ADD, SUB
            }
        """.trimIndent())
        assertEquals(listOf(HeaderDeclaration("_op", HeaderDeclarationType.ENUM)), HeaderParser.parse(rule))
    }
}