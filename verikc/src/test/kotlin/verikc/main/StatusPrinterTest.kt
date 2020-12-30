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

package verikc.main

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.base.symbol.SymbolContext
import verikc.lang.LangSymbol.FUNCTION_POSEDGE_BOOL
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT

internal class StatusPrinterTest {

    @Test
    fun `substitute lang type symbol`() {
        StatusPrinter.setSymbolContext(SymbolContext())
        assertStringEquals(
            "_ubit _unit",
            StatusPrinter.substituteSymbols("$TYPE_UBIT $TYPE_UNIT")
        )
    }

    @Test
    fun `substitute lang function symbol`() {
        StatusPrinter.setSymbolContext(SymbolContext())
        assertStringEquals(
            "posedge",
            StatusPrinter.substituteSymbols("$FUNCTION_POSEDGE_BOOL")
        )
    }

    @Test
    fun `substitute lang operator symbol`() {
        StatusPrinter.setSymbolContext(SymbolContext())
        assertStringEquals(
            "forever",
            StatusPrinter.substituteSymbols("$OPERATOR_FOREVER")
        )
    }

    @Test
    fun `substitute declaration symbol`() {
        val symbolContext = SymbolContext()
        val symbol = symbolContext.registerSymbol("x")
        StatusPrinter.setSymbolContext(symbolContext)
        assertStringEquals(
            "x",
            StatusPrinter.substituteSymbols("$symbol")
        )
    }
}
