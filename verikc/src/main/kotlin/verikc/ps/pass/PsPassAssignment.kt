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

package verikc.ps.pass

import verikc.base.ast.ActionBlockType
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.ps.ast.*

object PsPassAssignment: PsPassBase() {

    override fun passModule(module: PsModule) {
        module.actionBlocks.forEach { passActionBLock(it) }
    }

    private fun passActionBLock(actionBlock: PsActionBlock) {
        passStatement(actionBlock.block) {
            replace(it, actionBlock.actionBlockType == ActionBlockType.SEQ)
        }
    }

    private fun replace(statement: PsStatement, isSeq: Boolean): PsStatement? {
        if (statement is PsStatementExpression) {
            val expression = statement.expression
            if (expression is PsExpressionFunction) {
                if (expression.functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE) {
                    return PsStatementExpression(
                        statement.line,
                        PsExpressionFunction(
                            expression.line,
                            expression.typeReified,
                            if (isSeq) FUNCTION_NATIVE_ASSIGN_NONBLOCKING else FUNCTION_NATIVE_ASSIGN_BLOCKING,
                            expression.receiver,
                            expression.args
                        )
                    )
                }
            }
        }
        return null
    }
}
