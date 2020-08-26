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

package verik.core.it.symbol

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.assertThrowsMessage
import verik.core.base.LineException
import verik.core.it.ItBlock
import verik.core.it.ItExpressionOperator
import verik.core.it.ItReifiedType
import verik.core.it.ItTypeClass
import verik.core.lang.LangOperatorExtractorRequest
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.sv.SvBlock
import verik.core.sv.SvControlBlockType
import verik.core.sv.SvReifiedType
import verik.core.sv.SvStatementControlBlock

internal class ItSymbolTableTest {

    @Test
    fun `reify operator forever`() {
        val operator = ItExpressionOperator(
                0,
                LangSymbol.TYPE_UNIT,
                null,
                LangSymbol.OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                LangSymbol.TYPE_REIFIED_UNIT,
                symbolTable.reifyOperator(operator)
        )
    }

    @Test
    fun `extract uint`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8))
        val symbolTable = ItSymbolTable()
        val expected = SvReifiedType("logic", "[7:0]", "")
        Assertions.assertEquals(
                expected,
                symbolTable.extractType(reifiedType, 0)
        )
    }

    @Test
    fun `extract uint illegal type class`() {
        val reifiedType = ItReifiedType(TYPE_UINT, ItTypeClass.TYPE, listOf(8))
        val symbolTable = ItSymbolTable()
        assertThrowsMessage<LineException>("unable to extract type $TYPE_UINT(8) due to invalid type class") {
            symbolTable.extractType(reifiedType, 0)
        }
    }

    @Test
    fun `extract operator forever`() {
        val operator = ItExpressionOperator(
                0,
                LangSymbol.TYPE_UNIT,
                LangSymbol.TYPE_REIFIED_UNIT,
                LangSymbol.OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        val request = LangOperatorExtractorRequest(
                operator,
                null,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val expected = SvStatementControlBlock(
                0,
                SvControlBlockType.FOREVER,
                listOf(),
                listOf(SvBlock(0, listOf()))
        )
        val symbolTable = ItSymbolTable()
        Assertions.assertEquals(
                expected,
                symbolTable.extractOperator(request)
        )
    }
}