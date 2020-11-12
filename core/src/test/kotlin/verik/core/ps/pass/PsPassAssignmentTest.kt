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

package verik.core.ps.pass

import org.junit.jupiter.api.Test
import verik.core.base.ast.ActionBlockType
import verik.core.base.ast.Symbol
import verik.core.lang.LangSymbol.FUNCTION_ASSIGN_BOOL_BOOL
import verik.core.lang.LangSymbol.FUNCTION_BLOCK_ASSIGN
import verik.core.lang.LangSymbol.FUNCTION_NONBLOCK_ASSIGN
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.ps.ast.PsActionBlock
import verik.core.ps.ast.PsBlock
import verik.core.ps.ast.PsExpressionFunction
import verik.core.ps.ast.PsStatementExpression
import verik.core.ps.symbol.PsSymbolTable

internal class PsPassAssignmentTest {

    @Test
    fun `pass seq block`() {
        val block = PsBlock(0, arrayListOf(PsStatementExpression(
                0,
                PsExpressionFunction(0, TYPE_REIFIED_UNIT, FUNCTION_ASSIGN_BOOL_BOOL, null, arrayListOf())
        )))
        val actionBlock = PsActionBlock(0, "", Symbol(1, 1, 1), ActionBlockType.SEQ, listOf(), block)
        PsPassAssignment.passDeclaration(actionBlock, PsSymbolTable())
        val function = ((actionBlock.block.statements[0] as PsStatementExpression)
                .expression as PsExpressionFunction).function
        assert(function == FUNCTION_NONBLOCK_ASSIGN)
    }

    @Test
    fun `pass com block`() {
        val block = PsBlock(0, arrayListOf(PsStatementExpression(
                0,
                PsExpressionFunction(0, TYPE_REIFIED_UNIT, FUNCTION_ASSIGN_BOOL_BOOL, null, arrayListOf())
        )))
        val actionBlock = PsActionBlock(0, "", Symbol(1, 1, 1), ActionBlockType.COM, listOf(), block)
        PsPassAssignment.passDeclaration(actionBlock, PsSymbolTable())
        val function = ((actionBlock.block.statements[0] as PsStatementExpression)
                .expression as PsExpressionFunction).function
        assert(function == FUNCTION_BLOCK_ASSIGN)
    }
}