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
import verik.data.*

internal class RconfExpanderTest {

    @Test
    fun `empty list`() {
        val entries = RconfExpander.expand(i_RconfList("x"))
        assertEquals(listOf<RconfEntry>(), entries)
    }

    @Test
    fun `single entry`() {
        val list = i_RconfList("x")
        list.add(i_RconfEntry("y", u(1, 0), 0))
        val entries = RconfExpander.expand(list)
        assertEquals(1, entries.size)
        assertEquals("x/y", entries[0].name)
    }

    @Test
    fun `illegal name empty`() {
        assertThrowsMessage<IllegalArgumentException>("name cannot be empty") {
            RconfExpander.expand(i_RconfList(""))
        }
    }

    @Test
    fun `illegal name seed`() {
        assertThrowsMessage<IllegalArgumentException>("name \"SEED_0123abcd\" is reserved") {
            RconfExpander.expand(i_RconfList("SEED_0123abcd"))
        }
    }

    @Test
    fun `illegal name whitespace`() {
        assertThrowsMessage<IllegalArgumentException>("name \" \" cannot contain whitespace") {
            RconfExpander.expand(i_RconfList(" "))
        }
    }

    @Test
    fun `illegal name`() {
        assertThrowsMessage<IllegalArgumentException>("illegal characters in name \".\"") {
            RconfExpander.expand(i_RconfList("."))
        }
    }
}