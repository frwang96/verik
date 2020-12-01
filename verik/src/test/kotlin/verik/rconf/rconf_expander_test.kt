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

package verik.rconf

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.data.*

internal class _rconf_expander_test {

    @Test
    fun `empty list`() {
        val entries = _rconf_expander.expand(rconf_list("x"))
        assertEquals(listOf<_rconf_entry>(), entries)
    }

    @Test
    fun `single entry`() {
        val list = rconf_list("x")
        list.add(rconf_entry("y", uint(1, 0), 0))
        val entries = _rconf_expander.expand(list)
        val expected = listOf(rconf_entry("x/y", uint(1, 0), 0))
        assertEquals(expected, entries)
    }

    @Test
    fun `illegal name empty`() {
        assertThrowsMessage<IllegalArgumentException>("name cannot be empty") {
            _rconf_expander.expand(rconf_list(""))
        }
    }

    @Test
    fun `illegal name seed`() {
        assertThrowsMessage<IllegalArgumentException>("name \"SEED_0123abcd\" is reserved") {
            _rconf_expander.expand(rconf_list("SEED_0123abcd"))
        }
    }

    @Test
    fun `illegal name whitespace`() {
        assertThrowsMessage<IllegalArgumentException>("name \" \" cannot contain whitespace") {
            _rconf_expander.expand(rconf_list(" "))
        }
    }

    @Test
    fun `illegal name`() {
        assertThrowsMessage<IllegalArgumentException>("illegal characters in name \".\"") {
            _rconf_expander.expand(rconf_list("."))
        }
    }
}