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

package verikc.al

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol

internal class AlTreeParserTest {

    @Test
    fun `package valid`() {
        AlTreeParser.parseKotlinFile(Symbol.NULL, "package test")
    }

    @Test
    fun `import valid`() {
        AlTreeParser.parseKotlinFile(Symbol.NULL, "import test")
    }

    @Test
    fun `assignment valid`() {
        AlTreeParser.parseKotlinFile(Symbol.NULL, "val x = \"x\"")
    }

    @Test
    fun `function valid`() {
        AlTreeParser.parseKotlinFile(
            Symbol.NULL,
            """
                fun f(x: _int, y: _int): _int {
                    return x + y
                }
            """.trimIndent()
        )
    }

    @Test
    fun `class valid`() {
        AlTreeParser.parseKotlinFile(
            Symbol.NULL,
            """
                class c(val x: _int) {
                    fun add(y: _int): _int {
                        return x + y
                    }
                }
            """.trimIndent()
        )
    }

    @Test
    fun `enum valid`() {
        AlTreeParser.parseKotlinFile(
            Symbol.NULL,
            """
                enum class _bool(val value: _ubit = enum_sequential()) {
                    FALSE, TRUE;
                }
            """.trimIndent()
        )
    }

    @Test
    fun `syntax error`() {
        assertThrows<LineException> { AlTreeParser.parseKotlinFile(Symbol.NULL, "x") }
    }

    @Test
    fun `syntax illegal unicode`() {
        assertThrowsMessage<LineException>("only ASCII characters are permitted") {
            AlTreeParser.parseKotlinFile(Symbol.NULL, "val x = \"αβγ\"")
        }
    }
}
