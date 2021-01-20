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

package verik.rconf

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BitUtilTest {

    @Test
    fun `get hex string`() {
        assertEquals("0", BitUtil.get_hex_string(0, 0.toString()))
        assertEquals("0f", BitUtil.get_hex_string(6, 15.toString()))
        assertEquals("0f", BitUtil.get_hex_string(8, 15.toString()))
        assertEquals("ff", BitUtil.get_hex_string(8, (-1).toString()))
        assertEquals("01", BitUtil.get_hex_string(8, 0x01.toString()))
        assertEquals("12", BitUtil.get_hex_string(8, 0x12.toString()))
        assertEquals("fe", BitUtil.get_hex_string(8, 0xfe.toString()))
        assertEquals("123", BitUtil.get_hex_string(10, 0x123.toString()))
    }
}