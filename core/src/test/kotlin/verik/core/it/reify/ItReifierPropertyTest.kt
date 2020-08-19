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

package verik.core.it.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.base.Symbol

internal class ItReifierPropertyTest {

    @Test
    fun `port bool`() {
        val port = ItPort(
                0,
                "x",
                Symbol(1 ,1 ,1),
                TYPE_BOOL,
                null,
                ItPortType.INPUT,
                ItExpressionFunction(0, TYPE_BOOL, null, FUNCTION_BOOL, null, listOf())
        )
        ItReifierProperty.reifyDeclaration(port, ItSymbolTable())
        assertEquals(
                ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                port.typeReified
        )
    }
}