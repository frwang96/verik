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
    fun `from boolean`() {
        val value = LiteralValue.fromBoolean(true)
        assertTrue(value.toBoolean())
    }

    @Test
    fun `from int`() {
        assertStringEquals("32'hffff_fffe", LiteralValue.fromInt(-2))
        assertStringEquals("32'hffff_ffff", LiteralValue.fromInt(-1))
        assertStringEquals("32'h0000_0000", LiteralValue.fromInt(0))
        assertStringEquals("32'h0000_0001", LiteralValue.fromInt(1))
        assertStringEquals("32'h7fff_ffff", LiteralValue.fromInt(0x7fff_ffff))
    }

    @Test
    fun `from bit int`() {
        assertStringEquals("1'h1", LiteralValue.fromBitInt(1, 1, Line(0)))
        assertStringEquals("4'hf", LiteralValue.fromBitInt(4, 15, Line(0)))
        assertStringEquals("36'h0_0000_ffff", LiteralValue.fromBitInt(36, 0xffff, Line(0)))
    }

    @Test
    fun `from bit int illegal width`() {
        assertThrowsMessage<LineException>("unable to cast int literal 2 to width 1") {
            LiteralValue.fromBitInt(1, 2, Line(0))
        }
    }

    @Test
    fun `to int`() {
        assertEquals(-1, LiteralValue.fromInt(-1).toInt())
        assertEquals(0, LiteralValue.fromInt(0).toInt())
        assertEquals(1, LiteralValue.fromInt(1).toInt())
        assertEquals(15, LiteralValue.fromInt(0xf).toInt())
    }

    @Test
    fun `equality simple`() {
        assertTrue(LiteralValue.fromBoolean(true) == LiteralValue.fromBoolean(true))
        assertTrue(LiteralValue.fromInt(0) == LiteralValue.fromInt(0))
    }
}
