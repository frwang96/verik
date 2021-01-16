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
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.ps.ast.*

object PsPassConvertAssignment: PsPassBase() {

    override fun passModule(module: PsModule) {
        val modulePropertySymbols = HashSet<Symbol>()
        module.ports.forEach { modulePropertySymbols.add(it.property.symbol) }
        module.properties.forEach { modulePropertySymbols.add(it.symbol) }

        module.actionBlocks.forEach {
            if (it.actionBlockType == ActionBlockType.SEQ) {
                passBlockSeq(it.block, modulePropertySymbols)
            } else {
                passBlockNotSeq(it.block)
            }
        }
        module.methodBlocks.forEach { passBlockNotSeq(it.block) }
    }

    override fun passCls(cls: PsCls) {
        cls.methodBlocks.forEach { passBlockNotSeq(it.block) }
    }

    private fun passBlockSeq(block: PsBlock, modulePropertySymbols: Set<Symbol>) {
        PsPassUtil.replaceBlock(block) {
            if (!it.isSubexpression) replaceSeq(it.expression, modulePropertySymbols)
            else null
        }
    }

    private fun passBlockNotSeq(block: PsBlock) {
        PsPassUtil.replaceBlock(block) {
            if (!it.isSubexpression) replaceNotSeq(it.expression)
            else null
        }
    }

    private fun replaceSeq(expression: PsExpression, modulePropertySymbols: Set<Symbol>): PsExpression? {
        return if (expression is PsExpressionFunction
            && expression.functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        ) {
            var receiver = expression.receiver!!
            while (true) {
                receiver = when (receiver) {
                    is PsExpressionFunction -> {
                        if (receiver.receiver != null) receiver.receiver!!
                        else break
                    }
                    is PsExpressionProperty -> {
                        if (receiver.receiver != null) receiver.receiver!!
                        else break
                    }
                    else -> {
                        throw LineException("expression cannot be assigned to", expression.line)
                    }
                }
            }
            val receiverSymbol = if (receiver is PsExpressionProperty) {
                receiver.propertySymbol
            } else throw LineException("property expression expected", expression.line)
            val functionSymbol = if (receiverSymbol in modulePropertySymbols) {
                FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
            } else {
                FUNCTION_INTERNAL_ASSIGN_BLOCKING
            }
            PsExpressionFunction(
                expression.line,
                expression.typeGenerified,
                functionSymbol,
                expression.receiver,
                expression.args
            )
        } else null
    }

    private fun replaceNotSeq(expression: PsExpression): PsExpression? {
        return if (expression is PsExpressionFunction
            && expression.functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        ) {
            PsExpressionFunction(
                expression.line,
                expression.typeGenerified,
                FUNCTION_INTERNAL_ASSIGN_BLOCKING,
                expression.receiver,
                expression.args
            )
        } else null
    }
}
