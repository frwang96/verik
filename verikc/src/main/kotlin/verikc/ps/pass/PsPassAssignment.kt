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
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.ps.ast.*

object PsPassAssignment: PsPassBase() {

    override fun passModule(module: PsModule) {
        val modulePropertySymbols = HashSet<Symbol>()
        module.ports.forEach { modulePropertySymbols.add(it.symbol) }
        module.primaryProperties.forEach { modulePropertySymbols.add(it.symbol) }

        module.actionBlocks.forEach {
            passBlock(it.block, modulePropertySymbols, it.actionBlockType == ActionBlockType.SEQ)
        }
        module.methodBlocks.forEach {
            passBlock(it.block, modulePropertySymbols, false)
        }
    }

    private fun passBlock(block: PsBlock, modulePropertySymbols: Set<Symbol>, isSeq: Boolean) {
        block.expressions.indices.forEach {
            val replacement = replace(block.expressions[it], modulePropertySymbols, isSeq)
            if (replacement != null) {
                block.expressions[it] = replacement
            }
            val expression = block.expressions[it]
            if (expression is PsExpressionOperator) {
                expression.blocks.forEach { block -> passBlock(block, modulePropertySymbols, isSeq) }
            }
        }
    }

    private fun replace(expression: PsExpression, modulePropertySymbols: Set<Symbol>, isSeq: Boolean): PsExpression? {
        if (expression is PsExpressionFunction) {
            if (expression.functionSymbol == FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE) {
                if (isSeq) {
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
                        FUNCTION_NATIVE_ASSIGN_NONBLOCKING
                    } else {
                        FUNCTION_NATIVE_ASSIGN_BLOCKING
                    }
                    return PsExpressionFunction(
                        expression.line,
                        expression.typeReified,
                        functionSymbol,
                        expression.receiver,
                        expression.args
                    )
                } else {
                    return PsExpressionFunction(
                        expression.line,
                        expression.typeReified,
                        FUNCTION_NATIVE_ASSIGN_BLOCKING,
                        expression.receiver,
                        expression.args
                    )
                }
            }
        }
        return null
    }
}
