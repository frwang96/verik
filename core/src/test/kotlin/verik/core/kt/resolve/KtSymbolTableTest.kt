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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.kt.KtConstructorInvocation
import verik.core.kt.KtDeclarationBaseProperty
import verik.core.kt.KtDeclarationType
import verik.core.kt.KtExpressionLiteral
import verik.core.symbol.Symbol

internal class KtSymbolTableTest {

    @Test
    fun `match property`() {
        val symbolTable = KtSymbolTable()
        symbolTable.addPkg(Symbol(1, 0, 0))
        symbolTable.addFile(Symbol(1, 1, 0))
        val property = KtDeclarationBaseProperty(
                0,
                "x",
                Symbol(1, 1, 1),
                null,
                listOf(),
                KtExpressionLiteral(0, null, "0")
        )
        symbolTable.addProperty(property, Symbol(1, 1, 0), 0)
        assertEquals(
                property,
                symbolTable.matchProperty(Symbol(1, 1, 0), "x", 0)
        )
    }

    @Test
    fun `match property in child scope`() {
        val symbolTable = KtSymbolTable()
        symbolTable.addPkg(Symbol(1, 0, 0))
        symbolTable.addFile(Symbol(1, 1, 0))
        val type = KtDeclarationType(
                0,
                "_m",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                KtConstructorInvocation(0, "_module", listOf(), null),
                listOf(),
                listOf()
        )
        symbolTable.addType(type, Symbol(1, 1, 0), 0)
        val property = KtDeclarationBaseProperty(
                0,
                "x",
                Symbol(1, 1, 2),
                null,
                listOf(),
                KtExpressionLiteral(0, null, "0")
        )
        symbolTable.addProperty(property, Symbol(1, 1, 0), 0)
        assertEquals(
                property,
                symbolTable.matchProperty(Symbol(1, 1, 1), "x", 0)
        )
    }
}