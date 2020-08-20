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

package verik.core.lang

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import verik.core.base.Symbol
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UNIT

internal class LangFunctionTest {

    @Test
    fun `matches simple`() {
        val function = LangFunction(
                Symbol(0, 0, 0),
                null,
                listOf(),
                TYPE_UNIT,
                { },
                { null },
                ""
        )
        assertTrue(function.matches(null, listOf()))
    }

    @Test
    fun `matches target parent`() {
        val function = LangFunction(
                Symbol(0, 0, 0),
                TYPE_ANY,
                listOf(),
                TYPE_UNIT,
                { },
                { null },
                ""
        )
        assertTrue(function.matches(
                Lang.typeTable.parents(TYPE_INT, 0),
                listOf()
        ))
    }

    @Test
    fun `no match args`() {
        val function = LangFunction(
                Symbol(0, 0, 0),
                null,
                listOf(TYPE_INT),
                TYPE_UNIT,
                { },
                { null },
                ""
        )
        assertTrue(function.matches(
                null,
                listOf(listOf(TYPE_INT))
        ))
        assertFalse(function.matches(
                null,
                listOf(listOf(TYPE_ANY))
        ))
    }
}