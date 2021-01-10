/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.rsx.table

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.FILE_SYMBOL
import verikc.base.ast.Line
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rsx.RsxResolveUtil

internal class RsxSymbolTableTest {

    @Test
    fun `resolve type unit`() {
        val symbolTable = RsxResolveUtil.resolveSymbolTable("")
        assertEquals(
            TYPE_UNIT,
            symbolTable.resolveTypeSymbol("_unit", FILE_SYMBOL, Line(0))
        )
    }
}
