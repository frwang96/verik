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

import verikc.base.ast.ActionBlockType
import verikc.lang.LangSymbol.FUNCTION_BLOCK_ASSIGN
import verikc.lang.LangSymbol.FUNCTION_NONBLOCK_ASSIGN
import verikc.lang.modules.LangModuleAssignment
import verikc.ps.ast.*
import verikc.ps.symbol.PsSymbolTable

object PsPassAssignment: PsPassBase() {

    override fun passModule(module: PsModule, symbolTable: PsSymbolTable) {
        module.actionBlocks.forEach { passActionBlock(it, symbolTable) }
    }

    override fun passActionBlock(actionBlock: PsActionBlock, symbolTable: PsSymbolTable) {
        passStatement(actionBlock.block) {
            pass(it, actionBlock.actionBlockType == ActionBlockType.SEQ)
        }
    }

    private fun pass(statement: PsStatement, isSeq: Boolean): List<PsStatement>? {
        if (statement is PsStatementExpression) {
            val expression = statement.expression
            if (expression is PsExpressionFunction) {
                if (LangModuleAssignment.isAssign(expression.functionSymbol)) {
                    return listOf(
                        PsStatementExpression(
                            statement.line,
                            PsExpressionFunction(
                                expression.line,
                                expression.typeReified,
                                if (isSeq) FUNCTION_NONBLOCK_ASSIGN else FUNCTION_BLOCK_ASSIGN,
                                expression.receiver,
                                expression.args
                            )
                        )
                    )
                }
            }
        }
        return null
    }
}
