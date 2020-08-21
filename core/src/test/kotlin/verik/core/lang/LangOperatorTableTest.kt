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
import verik.core.it.ItBlock
import verik.core.it.ItExpressionOperator
import verik.core.kt.KtExpressionOperator
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.SvBlock
import verik.core.sv.SvControlBlockType
import verik.core.sv.SvStatementControlBlock

internal class LangOperatorTableTest {

    @Test
    fun `resolve forever`() {
        val operator = KtExpressionOperator(
                0,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf()
        )
        assertEquals(
                TYPE_UNIT,
                Lang.operatorTable.resolve(operator)
        )
    }

    @Test
    fun `reify forever`() {
        val operator = ItExpressionOperator(
                0,
                TYPE_UNIT,
                null,
                OPERATOR_FOREVER,
                null,
                listOf(),
                listOf(ItBlock(0, listOf()))
        )
        assertEquals(
                TYPE_REIFIED_UNIT,
                Lang.operatorTable.reify(operator)
        )
    }

    @Test
    fun `extract forever`() {
        val operator = ItExpressionOperator(
                0,
                TYPE_UNIT,
                TYPE_REIFIED_UNIT,
                OPERATOR_FOREVER,
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
        assertEquals(
                expected,
                Lang.operatorTable.extract(request)
        )
    }
}