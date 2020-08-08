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

package io.verik.core.al

import io.verik.core.FileLineException
import io.verik.core.assert.assertThrowsMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AlRuleParserTest {

    @Test
    fun `package valid`() {
        AlRuleParser.parseKotlinFile("package com")
    }

    @Test
    fun `import valid`() {
        AlRuleParser.parseKotlinFile("import com")
    }

    @Test
    fun `assignment valid`() {
        AlRuleParser.parseKotlinFile("val x = \"x\"")
    }

    @Test
    fun `function valid`() {
        AlRuleParser.parseKotlinFile("fun f(x: Int, y: Int) = x + y")
        AlRuleParser.parseKotlinFile("""
            fun f(x: Int, y: Int): Int {
                return x + y
            }
        """.trimIndent())
    }

    @Test
    fun `class valid`() {
        AlRuleParser.parseKotlinFile("""
            class c(val x: Int = 0): Any() {
                fun add(y: Int): Int {
                    return x + y
                }
            }
        """.trimIndent())
    }

    @Test
    fun `enum valid`() {
        AlRuleParser.parseKotlinFile("""
            enum class _bool {
                FALSE, TRUE;
            } fun _bool() = _bool.values()[0]
        """.trimIndent())
    }

    @Test
    fun `syntax error`() {
        assertThrows<FileLineException> { AlRuleParser.parseKotlinFile("x") }
    }

    @Test
    fun `syntax illegal unicode`() {
        assertThrowsMessage<FileLineException>("only ASCII characters are permitted") {
            AlRuleParser.parseKotlinFile("val x = \"αβγ\"")
        }
    }

    @Test
    fun `rule unsupported`() {
        assertThrowsMessage<FileLineException>("lexer token type \"ShebangLine\" not supported") {
            AlRuleParser.parseKotlinFile("#!\n")
        }
    }

    @Test
    fun `token unsupported`() {
        assertThrowsMessage<FileLineException>("lexer token type \"TRY\" not supported") {
            AlRuleParser.parseKotlinFile("""
                fun f(x: String) {
                    try {
                        print(x)
                    } catch (e: Exception) {}
                }
            """.trimIndent())
        }
    }
}