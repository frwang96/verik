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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.symbol.Symbol

internal class AlTreeSerializerTest {

    @Test
    fun `serialize deserialize package`() {
        val string = """
            package test
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(Symbol.NULL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(byteArray)
        )
    }

    @Test
    fun `serialize deserialize function`() {
        val string = """
            fun f(x: _int, y: _int): _int {
                return x + y
            }
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(Symbol.NULL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(byteArray)
        )
    }

    @Test
    fun `serialize deserialize class`() {
        val string = """
            class c(val x: _int) {
                fun add(y: _int): _int {
                    return x + y
                }
            }
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(Symbol.NULL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(byteArray)
        )
    }
}