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

package verik.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class _data_test {

    @Test
    fun `ubit equals`() {
        assert(ubit(8, 0) == ubit(8, 0))
        assert(ubit(8, 0) != ubit(8, 1))
        assert(ubit(8, 1) == ubit(8, 1))
    }

    @Test
    fun `ubit to string`() {
        assertEquals("0x0", ubit(0, 0).toString())
        assertEquals("0x0f", ubit(6, 15).toString())
        assertEquals("0x0f", ubit(8, 15).toString())
        assertEquals("0xff", ubit(8, -1).toString())
        assertEquals("0x01", ubit(8, 0x01).toString())
        assertEquals("0x12", ubit(8, 0x12).toString())
        assertEquals("0xfe", ubit(8, 0xfe).toString())
        assertEquals("0x123", ubit(10, 0x123).toString())
    }

    @Test
    fun `sbit equals`() {
        assert(sbit(8, 0) == sbit(8, 0))
        assert(sbit(8, 0) != sbit(8, 1))
        assert(sbit(8, 1) == sbit(8, 1))
    }

    @Test
    fun `sbit to string`() {
        assertEquals("0x0", sbit(0, 0).toString())
        assertEquals("0x0f", sbit(6, 15).toString())
        assertEquals("0x0f", sbit(8, 15).toString())
        assertEquals("0xff", sbit(8, -1).toString())
        assertEquals("0x01", sbit(8, 0x01).toString())
        assertEquals("0x12", sbit(8, 0x12).toString())
        assertEquals("0xfe", sbit(8, 0xfe).toString())
        assertEquals("0x123", sbit(10, 0x123).toString())
    }
}