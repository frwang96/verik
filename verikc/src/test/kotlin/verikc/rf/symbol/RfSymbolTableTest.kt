/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rf.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.line
import verikc.rf.RfReifyUtil

internal class RfSymbolTableTest {

    @Test
    fun `get parent type symbols data`() {
        val symbolTable = RfReifyUtil.reifyDeclarationSymbolTable("")
        assertEquals(
            listOf(TYPE_DATA, TYPE_INSTANCE, TYPE_ANY),
            symbolTable.getParentTypeSymbols(TYPE_DATA, line(0))
        )
    }
}