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

package io.verik.core.kt.resolve

import io.verik.core.assertThrowsMessage
import io.verik.core.main.LineException
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtScopeResolverTest  {

    @Test
    fun `parent is file`() {
        val resolver = KtScopeResolver()
        resolver.addFile(
                Symbol(1, 1, 0),
                listOf(Symbol(1, 0, 0))
        )
        assertEquals(
                listOf(Symbol(1, 0, 0)),
                resolver.resolutionEntries(Symbol(1, 1, 0), 0)
        )
    }

    @Test
    fun `parent is declaration`() {
        val resolver = KtScopeResolver()
        resolver.addFile(
                Symbol(1, 1, 0),
                listOf(Symbol(1, 0, 0))
        )
        resolver.addScope(
                Symbol(1, 1, 1),
                Symbol(1, 1, 0),
                0
        )
        assertEquals(
                listOf(Symbol(1, 1, 1), Symbol(1, 0, 0)),
                resolver.resolutionEntries(Symbol(1, 1, 1), 0)
        )
    }

    @Test
    fun `parent file not defined`() {
        val resolver = KtScopeResolver()
        assertThrowsMessage<LineException>("resolution entries of file symbol (1, 1, 0) have not been defined") {
            resolver.addScope(
                    Symbol(1, 1, 1),
                    Symbol(1, 1, 0),
                    0
            )
        }
    }

    @Test
    fun `parent declaration not defined`() {
        val resolver = KtScopeResolver()
        assertThrowsMessage<LineException>("parent of scope (1, 1, 1) has not been defined") {
            resolver.addScope(
                    Symbol(1, 1, 2),
                    Symbol(1, 1, 1),
                    0
            )
        }
    }
}