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
    fun `uint equals`() {
        assert(uint(8, 0) == uint(8, 0))
        assert(uint(8, 0) != uint(8, 1))
        assert(uint(8, 1) == uint(8, 1))
    }

    @Test
    fun `uint to string`() {
        assertEquals("0x0", uint(0, 0).toString())
        assertEquals("0x0f", uint(6, 15).toString())
        assertEquals("0x0f", uint(8, 15).toString())
        assertEquals("0xff", uint(8, -1).toString())
        assertEquals("0x01", uint(8, 0x01).toString())
        assertEquals("0x12", uint(8, 0x12).toString())
        assertEquals("0xfe", uint(8, 0xfe).toString())
        assertEquals("0x123", uint(10, 0x123).toString())
    }

    @Test
    fun `sint equals`() {
        assert(sint(8, 0) == sint(8, 0))
        assert(sint(8, 0) != sint(8, 1))
        assert(sint(8, 1) == sint(8, 1))
    }

    @Test
    fun `sint to string`() {
        assertEquals("0x0", sint(0, 0).toString())
        assertEquals("0x0f", sint(6, 15).toString())
        assertEquals("0x0f", sint(8, 15).toString())
        assertEquals("0xff", sint(8, -1).toString())
        assertEquals("0x01", sint(8, 0x01).toString())
        assertEquals("0x12", sint(8, 0x12).toString())
        assertEquals("0xfe", sint(8, 0xfe).toString())
        assertEquals("0x123", sint(10, 0x123).toString())
    }
}