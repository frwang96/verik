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

package verik.common.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TypesTest {

    @Test
    fun `use byte`() {
        assertEquals("0x00", byte(0).toString())
        assertEquals("0x1f", byte(0x1f).toString())
    }

    @Test
    fun `use short`() {
        assertEquals("0x0000", short(0).toString())
        assertEquals("0x1fff", short(0x1fff).toString())
    }

    @Test
    fun `use int`() {
        assertEquals("0x00000000", int(0).toString())
        assertEquals("0x1fffffff", int(0x1fffffff).toString())
    }
}