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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.base.LineException
import verik.core.sv.SvTypeReified

internal class LangTypeTableTest {

    @Test
    fun `resolve unit`() {
        assertEquals(
                TYPE_UNIT,
                Lang.typeTable.resolve("Unit")
        )
    }

    @Test
    fun `extract uint`() {
        val typeReified = ItTypeReified(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8))
        val expected = SvTypeReified("logic", "[7:0]", "")
        assertEquals(expected, Lang.typeTable.extract(typeReified, 0))
    }

    @Test
    fun `extract uint illegal type class`() {
        val typeReified = ItTypeReified(TYPE_UINT, ItTypeClass.TYPE, listOf(8))
        assertThrowsMessage<LineException>("type instance of type class instance expected") {
            Lang.typeTable.extract(typeReified, 0)
        }
    }
}