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

package verik.core.base

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class BitArrayTest {

    @Test
    fun `bit false`() {
        val bitArray = BitArray(true)
        assertTrue(bitArray[0])
        for (x in 1..31) {
            assertFalse(bitArray[x])
        }
    }

    @Test
    fun `int positive`() {
        val bitArray = BitArray(1, 2)
        assertTrue(bitArray[0])
        assertFalse(bitArray[1])
        assertFalse(bitArray[2])
    }

    @Test
    fun `int negative`() {
        val bitArray = BitArray(-1, 2)
        assertTrue(bitArray[0])
        assertTrue(bitArray[1])
        assertFalse(bitArray[2])
    }
}