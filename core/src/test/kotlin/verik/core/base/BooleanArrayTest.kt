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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import verik.core.assertStringEquals

internal class BooleanArrayTest {

    @Test
    fun `from boolean`() {
        val booleanArray = BooleanArray.fromBoolean(true)
        assertTrue(booleanArray[0])
    }

    @Test
    fun `from int implicit`() {
        assertStringEquals("10", BooleanArray.fromIntImplicit(-2))
        assertStringEquals("1", BooleanArray.fromIntImplicit(-1))
        assertStringEquals("0", BooleanArray.fromIntImplicit(0))
        assertStringEquals("01", BooleanArray.fromIntImplicit(1))
        assertStringEquals("010", BooleanArray.fromIntImplicit(2))
    }

    @Test
    fun `from int explicit`() {
        assertStringEquals("01111", BooleanArray.fromIntExplicit(0xf, 4))
        assertStringEquals("01", BooleanArray.fromIntExplicit(0b1, 1))
    }

    @Test
    fun `to int`() {
        assertEquals(-1, BooleanArray.fromIntImplicit(-1).toInt())
        assertEquals(0, BooleanArray.fromIntImplicit(0).toInt())
        assertEquals(1, BooleanArray.fromIntImplicit(1).toInt())
        assertEquals(15, BooleanArray.fromIntExplicit(0xf, 4).toInt())
    }

    @Test
    fun `equality simple`() {
        assertTrue(BooleanArray.fromBoolean(true) == BooleanArray.fromBoolean(true))
        assertTrue(BooleanArray.fromIntImplicit(0) == BooleanArray.fromIntImplicit(0))
    }
}