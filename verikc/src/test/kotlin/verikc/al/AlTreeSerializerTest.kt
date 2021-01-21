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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.FILE_SYMBOL

internal class AlTreeSerializerTest {

    @Test
    fun `serialize deserialize package`() {
        val string = """
            package test
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(FILE_SYMBOL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(FILE_SYMBOL, byteArray)
        )
    }

    @Test
    fun `serialize deserialize function`() {
        val string = """
            fun f(x: Int, y: Int): Int {
                return x + y
            }
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(FILE_SYMBOL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(FILE_SYMBOL, byteArray)
        )
    }

    @Test
    fun `serialize deserialize class`() {
        val string = """
            class C(val x: Int) {
                fun add(y: Int): Int {
                    return x + y
                }
            }
        """.trimIndent()
        val tree = AlTreeParser.parseKotlinFile(FILE_SYMBOL, string)
        val byteArray = AlTreeSerializer.serialize(tree)
        assertEquals(
            tree,
            AlTreeSerializer.deserialize(FILE_SYMBOL, byteArray)
        )
    }

    @Test
    fun `serialize fail short`() {
        val byteArray = ByteArray(0)
        assertEquals(
            null,
            AlTreeSerializer.deserialize(FILE_SYMBOL, byteArray)
        )
    }

    @Test
    fun `serialize fail long`() {
        val tree = AlTreeParser.parseKotlinFile(FILE_SYMBOL, "")
        val byteArray = AlTreeSerializer.serialize(tree)
        val extendedByteArray = ByteArray(byteArray.size + 1)
        byteArray.copyInto(extendedByteArray)

        assertEquals(
            null,
            AlTreeSerializer.deserialize(FILE_SYMBOL, extendedByteArray)
        )
    }
}