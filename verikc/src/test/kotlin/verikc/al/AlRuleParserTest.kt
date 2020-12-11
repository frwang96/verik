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

package verikc.al

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.Symbol

internal class AlRuleParserTest {

    @Test
    fun `package valid`() {
        AlRuleParser.parseKotlinFile(Symbol.NULL, "package com")
    }

    @Test
    fun `import valid`() {
        AlRuleParser.parseKotlinFile(Symbol.NULL, "import com")
    }

    @Test
    fun `assignment valid`() {
        AlRuleParser.parseKotlinFile(Symbol.NULL, "val x = \"x\"")
    }

    @Test
    fun `function valid`() {
        AlRuleParser.parseKotlinFile(Symbol.NULL, "fun f(x: Int, y: Int) = x + y")
        AlRuleParser.parseKotlinFile(
            Symbol.NULL, """
            fun f(x: Int, y: Int): Int {
                return x + y
            }
        """.trimIndent())
    }

    @Test
    fun `class valid`() {
        AlRuleParser.parseKotlinFile(
            Symbol.NULL, """
            class c(val x: Int = 0): Any() {
                fun add(y: Int): Int {
                    return x + y
                }
            }
        """.trimIndent())
    }

    @Test
    fun `enum valid`() {
        AlRuleParser.parseKotlinFile(
            Symbol.NULL, """
            enum class _bool {
                FALSE, TRUE;
            } fun _bool() = _bool.values()[0]
        """.trimIndent())
    }

    @Test
    fun `syntax error`() {
        assertThrows<LineException> { AlRuleParser.parseKotlinFile(Symbol.NULL, "x") }
    }

    @Test
    fun `syntax illegal unicode`() {
        assertThrowsMessage<LineException>("only ASCII characters are permitted") {
            AlRuleParser.parseKotlinFile(Symbol.NULL, "val x = \"αβγ\"")
        }
    }

    @Test
    fun `rule unsupported`() {
        assertThrowsMessage<LineException>("lexer token type \"ShebangLine\" not supported") {
            AlRuleParser.parseKotlinFile(Symbol.NULL, "#!\n")
        }
    }

    @Test
    fun `token unsupported`() {
        assertThrowsMessage<LineException>("lexer token type \"TRY\" not supported") {
            AlRuleParser.parseKotlinFile(
                Symbol.NULL, """
                fun f(x: String) {
                    try {
                        print(x)
                    } catch (e: Exception) {}
                }
            """.trimIndent())
        }
    }
}
