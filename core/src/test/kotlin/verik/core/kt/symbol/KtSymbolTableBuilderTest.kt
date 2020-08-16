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

package verik.core.kt.symbol

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.kt.*
import verik.core.main.symbol.Symbol

internal class KtSymbolTableBuilderTest {

    @Test
    fun `resolve property in file`() {
        val property = KtDeclarationBaseProperty(
                0,
                "x",
                Symbol(1, 1, 1),
                null,
                listOf(),
                KtExpressionLiteral(0, null, "0")
        )
        val file = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf(property)
        )
        val symbolTable = KtSymbolTableBuilder.build(file, KtUtil.getSymbolContext())
        assertEquals(property, symbolTable.resolveProperty(Symbol(1, 1, 0), "x", 0))
    }

    @Test
    fun `resolve parameter in function`() {
        val property = KtDeclarationParameter(
                0,
                "x",
                Symbol(1, 1, 2),
                null,
                "Int",
                null
        )
        val file = KtFile(
                Symbol(1, 1, 0),
                listOf(),
                listOf(KtDeclarationFunction(
                        0,
                        "f",
                        Symbol(1, 1, 1),
                        listOf(),
                        listOf(property),
                        "Unit",
                        KtBlock(0, listOf()),
                        null
                ))
        )
        val symbolTable = KtSymbolTableBuilder.build(file, KtUtil.getSymbolContext())
        assertEquals(property, symbolTable.resolveProperty(Symbol(1, 1, 1), "x", 0))
    }
}