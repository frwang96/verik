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
import verik.core.base.LineException
import verik.core.it.ItReifiedType
import verik.core.it.ItTypeClass
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_DATA
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.sv.SvReifiedType

internal class LangTypeTableTest {

    @Test
    fun `parents uint`() {
        assertEquals(
                listOf(TYPE_UINT, TYPE_DATA, TYPE_INSTANCE, TYPE_ANY),
                Lang.typeTable.parents(TYPE_UINT, 0)
        )
    }

    @Test
    fun `extract uint`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8))
        val expected = SvReifiedType("logic", "[7:0]", "")
        assertEquals(expected, Lang.typeTable.extract(reifiedType, 0))
    }

    @Test
    fun `extract uint illegal type class`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.TYPE, listOf(8))
        assertThrowsMessage<LineException>("reified type of type class instance expected") {
            Lang.typeTable.extract(reifiedType, 0)
        }
    }
}