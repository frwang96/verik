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

package io.verik.core.lang

import io.verik.core.lang.LangSymbol.FUN_BOOL_INVOKE
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LangFunctionTableTest {

    @Test
    fun `no match`() {
        assertEquals(
                LangFunctionTableMatchNone,
                Lang.functionTable.match("none", listOf())
        )
    }

    @Test
    fun `match bool invoke`() {
        assertEquals(
                LangFunctionTableMatchSingle(FUN_BOOL_INVOKE, TYPE_BOOL),
                Lang.functionTable.match("_bool", listOf())
        )
    }
}