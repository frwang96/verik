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

package verik.common

import verik.assert.assertThrowsMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class UtilsTest {

    @Test
    fun `log function`() {
        assertThrowsMessage<IllegalArgumentException>("value must be positive") {
            assertEquals(0, log(-1))
        }
        assertThrowsMessage<IllegalArgumentException>("value must be positive") {
            assertEquals(0, log(0))
        }
        assertEquals(0, log(1))
        assertEquals(1, log(2))
        assertEquals(2, log(3))
        assertEquals(2, log(4))
        assertEquals(3, log(5))
        assertEquals(3, log(8))
        assertEquals(4, log(9))
    }

    @Test
    fun `exp function`() {
        assertThrowsMessage<IllegalArgumentException>("value must not be negative") {
            assertEquals(0, exp(-1))
        }
        assertThrowsMessage<IllegalArgumentException>("value out of range") {
            assertEquals(0, exp(31))
        }
        assertEquals(2, exp(1))
        assertEquals(4, exp(2))
    }
}