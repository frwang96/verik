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

package verikc.ps.pass

import org.junit.jupiter.api.Test
import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.base.ast.Symbol
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.ps.ast.PsActionBlock
import verikc.ps.ast.PsBlock
import verikc.ps.ast.PsExpressionFunction
import verikc.ps.ast.PsStatementExpression
import verikc.ps.symbol.PsSymbolTable

internal class PsPassAssignmentTest {

    @Test
    fun `pass seq block`() {
        val block = PsBlock(
            Line(0), arrayListOf(
                PsStatementExpression(
                    Line(0),
                    PsExpressionFunction(
                        Line(0),
                        TYPE_REIFIED_UNIT,
                        FUNCTION_ASSIGN_INSTANCE_INSTANCE,
                        null,
                        arrayListOf()
                    )
                )
            )
        )
        val actionBlock = PsActionBlock(Line(0), "", Symbol(3), ActionBlockType.SEQ, listOf(), block)
        PsPassAssignment.passDeclaration(actionBlock, PsSymbolTable())
        val function = ((actionBlock.block.statements[0] as PsStatementExpression)
            .expression as PsExpressionFunction).functionSymbol
        assert(function == FUNCTION_ASSIGN_NONBLOCKING)
    }

    @Test
    fun `pass com block`() {
        val block = PsBlock(
            Line(0), arrayListOf(
                PsStatementExpression(
                    Line(0),
                    PsExpressionFunction(
                        Line(0),
                        TYPE_REIFIED_UNIT,
                        FUNCTION_ASSIGN_INSTANCE_INSTANCE,
                        null,
                        arrayListOf()
                    )
                )
            )
        )
        val actionBlock = PsActionBlock(Line(0), "", Symbol(3), ActionBlockType.COM, listOf(), block)
        PsPassAssignment.passDeclaration(actionBlock, PsSymbolTable())
        val function = ((actionBlock.block.statements[0] as PsStatementExpression)
            .expression as PsExpressionFunction).functionSymbol
        assert(function == FUNCTION_ASSIGN_BLOCKING)
    }
}
