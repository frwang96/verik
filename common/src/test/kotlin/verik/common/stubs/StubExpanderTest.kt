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

package verik.common.stubs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.common.data.*

internal class StubExpanderTest {

    @Test
    fun `empty list`() {
        val stubEntries = StubExpander.expand(listOf())
        assertEquals(listOf<StubEntry>(), stubEntries)
    }

    @Test
    fun `stub entry`() {
        val stubEntries = StubExpander.expand(listOf(StubEntry("x", _uint(0))))
        val expected = listOf(StubEntry("x", _uint(0)))
        assertEquals(expected, stubEntries)
    }

    @Test
    fun `illegal name empty`() {
        assertThrowsMessage<IllegalArgumentException>("stub name cannot be empty") {
            StubExpander.expand(listOf(StubEntry("", _uint(0))))
        }
    }

    @Test
    fun `illegal name seed`() {
        assertThrowsMessage<IllegalArgumentException>("stub name SEED_0123abcd is reserved") {
            StubExpander.expand(listOf(StubEntry("SEED_0123abcd", _uint(0))))
        }
    }

    @Test
    fun `illegal name whitespace`() {
        assertThrowsMessage<IllegalArgumentException>("stub name \" \" cannot contain whitespace") {
            StubExpander.expand(listOf(StubEntry(" ", _uint(0))))
        }
    }

    @Test
    fun `illegal name`() {
        assertThrowsMessage<IllegalArgumentException>("illegal characters in stub name \".\"") {
            StubExpander.expand(listOf(StubEntry(".", _uint(0))))
        }
    }
}