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

package io.verik.stubs

import io.verik.common.data.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        val exception = assertThrows<IllegalArgumentException>("") {
            StubExpander.expand(listOf(StubEntry("", _uint(0))))
        }
        assertEquals("stub name cannot be empty", exception.message)
    }

    @Test
    fun `illegal name whitespace`() {
        val exception = assertThrows<IllegalArgumentException>("") {
            StubExpander.expand(listOf(StubEntry(" ", _uint(0))))
        }
        assertEquals("stub name \" \" cannot contain whitespace", exception.message)
    }

    @Test
    fun `illegal name`() {
        val exception = assertThrows<IllegalArgumentException>("") {
            StubExpander.expand(listOf(StubEntry(".", _uint(0))))
        }
        assertEquals("illegal characters in stub name \".\"", exception.message)
    }
}