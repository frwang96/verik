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

package verikc.base

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.assertThrowsMessage
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue

internal class LiteralValueTest {

    @Test
    fun `encode boolean`() {
        val value = LiteralValue.encodeBoolean(true)
        assertTrue(value.decodeBoolean())
    }

    @Test
    fun `encode int`() {
        assertEquals(-2, LiteralValue.encodeInt(-2).decodeInt())
        assertEquals(-1, LiteralValue.encodeInt(-1).decodeInt())
        assertEquals(0, LiteralValue.encodeInt(0).decodeInt())
        assertEquals(1, LiteralValue.encodeInt(1).decodeInt())
        assertEquals(0x7fff_ffff, LiteralValue.encodeInt(0x7fff_ffff).decodeInt())
    }

    @Test
    fun `encode string`() {
        assertStringEquals("", LiteralValue.encodeString("").decodeString())
        assertStringEquals("abc", LiteralValue.encodeString("abc").decodeString())
        assertStringEquals("123456789", LiteralValue.encodeString("123456789").decodeString())
    }

    @Test
    fun `encode bit int`() {
        assertStringEquals("1'1", LiteralValue.encodeBitInt(1, 1, Line(0)))
        assertStringEquals("4'f", LiteralValue.encodeBitInt(4, 15, Line(0)))
        assertStringEquals("36'0_0000_ffff", LiteralValue.encodeBitInt(36, 0xffff, Line(0)))
    }

    @Test
    fun `encode bit int illegal width`() {
        assertThrowsMessage<LineException>("unable to cast int literal 2 to width 1") {
            LiteralValue.encodeBitInt(1, 2, Line(0))
        }
    }

    @Test
    fun `equality simple`() {
        assertTrue(LiteralValue.encodeBoolean(true) == LiteralValue.encodeBoolean(true))
        assertTrue(LiteralValue.encodeInt(0) == LiteralValue.encodeInt(0))
    }
}
