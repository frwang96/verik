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

package verik.core.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.rf.symbol.RfSymbolTable
import verik.core.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.base.ast.Symbol
import verik.core.rf.ast.*

internal class RfReifierPropertyTest {

    @Test
    fun `port bool`() {
        val port = RfPort(
                0,
                "x",
                Symbol(1 ,1 ,1),
                TYPE_BOOL,
                null,
                RfPortType.INPUT,
                RfExpressionFunction(0, TYPE_BOOL, null, FUNCTION_TYPE_BOOL, null, listOf())
        )
        RfReifierProperty.reifyDeclaration(port, RfSymbolTable())
        assertEquals(
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                port.reifiedType
        )
    }
}