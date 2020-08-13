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

import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_SINT
import io.verik.core.lang.LangSymbol.TYPE_UNIT
import io.verik.core.svx.SvxType
import io.verik.core.vkx.VkxType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LangTypeTableTest {

    @Test
    fun `resolve unit type`() {
        assertEquals(
                TYPE_UNIT,
                Lang.typeTable.resolve("Unit")
        )
    }

    @Test
    fun `extract bool type`() {
        assertEquals(
                SvxType("logic", "", ""),
                Lang.typeTable.extract(VkxType(TYPE_BOOL, listOf()))
        )
    }

    @Test
    fun `extract sint type`() {
        assertEquals(
                SvxType("logic signed", "[7:0]", ""),
                Lang.typeTable.extract(VkxType(TYPE_SINT, listOf(8)))
        )
    }
}