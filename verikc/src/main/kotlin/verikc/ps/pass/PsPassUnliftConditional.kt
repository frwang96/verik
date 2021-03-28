/*
 * Copyright (c) 2021 Francis Wang
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

/*
 * Copyright (c) 2021 Francis Wang
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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_WHEN_BODY
import verikc.lang.LangSymbol.OPERATOR_WHEN_WRAPPER
import verikc.ps.ast.*

object PsPassUnliftConditional: PsPassBase() {

    private val NULL_EXPRESSION = PsExpressionProperty(Line(0), Symbol.NULL.toTypeGenerified(), Symbol.NULL, null)

    override fun passBlock(block: PsBlock) {
        block.expressions.forEachIndexed { index, expression ->
            passExpression(expression)?.let { block.expressions[index] = it }
        }
        block.expressions.forEach {
            if (it is PsExpressionOperator) {
                it.blocks.forEach { block -> passBlock(block) }
            }
        }
    }

    private fun passExpression(expression: PsExpression): PsExpression? {
        val leaf = (splitLeafExpression(expression) ?: return null) as PsExpressionOperator
        val blocks = if (leaf.operatorSymbol == OPERATOR_WHEN_WRAPPER) {
            val whenBodyOperator = leaf.blocks[0].expressions[0]
            if (whenBodyOperator is PsExpressionOperator && whenBodyOperator.operatorSymbol == OPERATOR_WHEN_BODY) {
                whenBodyOperator.blocks
            } else throw LineException("when body operator expected", leaf.line)
        } else {
            leaf.blocks
        }

        blocks.forEach {
            val substitute = expression.copy(it.line)
            substituteLeafExpression(substitute, it.expressions.last())
            it.expressions[it.expressions.size - 1] = substitute
        }

        return leaf
    }

    private fun splitLeafExpression(expression: PsExpression): PsExpression? {
        when (expression) {
            is PsExpressionFunction -> {
                @Suppress("DuplicatedCode")
                expression.receiver?.also {
                    if (isConditional(it)) {
                        expression.receiver = NULL_EXPRESSION
                        return it
                    }
                    splitLeafExpression(it)?.let { leaf -> return leaf }
                }
                expression.args.forEachIndexed { index, it ->
                    if (isConditional(it)) {
                        expression.args[index] = NULL_EXPRESSION
                        return it
                    }
                    splitLeafExpression(it)?.let { leaf -> return leaf }
                }
            }
            is PsExpressionOperator -> {
                expression.receiver?.also {
                    if (isConditional(it)) {
                        expression.receiver = NULL_EXPRESSION
                        return it
                    }
                    splitLeafExpression(it)?.let { leaf -> return leaf }
                }
                expression.args.forEachIndexed { index, it ->
                    if (isConditional(it)) {
                        expression.args[index] = NULL_EXPRESSION
                        return it
                    }
                    splitLeafExpression(it)?.let { leaf -> return leaf }
                }
            }
            is PsExpressionProperty -> {
                expression.receiver?.also {
                    if (isConditional(it)) {
                        expression.receiver = NULL_EXPRESSION
                        return it
                    }
                    splitLeafExpression(it)?.let { leaf -> return leaf }
                }
            }
            is PsExpressionLiteral -> {}
        }
        return null
    }

    private fun substituteLeafExpression(expression: PsExpression, leaf: PsExpression) {
        when (expression) {
            is PsExpressionFunction -> {
                @Suppress("DuplicatedCode")
                expression.receiver?.let {
                    if (isNullExpression(it)) {
                        expression.receiver = leaf
                        return
                    }
                    substituteLeafExpression(it, leaf)
                }
                expression.args.forEachIndexed { index, it ->
                    if (isNullExpression(it)) {
                        expression.args[index] = leaf
                        return
                    }
                    substituteLeafExpression(it, leaf)
                }
            }
            is PsExpressionOperator -> {
                expression.receiver?.let {
                    if (isNullExpression(it)) {
                        expression.receiver = leaf
                        return
                    }
                    substituteLeafExpression(it, leaf)
                }
                expression.args.forEachIndexed { index, it ->
                    if (isNullExpression(it)) {
                        expression.args[index] = leaf
                        return
                    }
                    substituteLeafExpression(it, leaf)
                }
            }
            is PsExpressionProperty -> {
                expression.receiver?.let {
                    if (isNullExpression(it)) {
                        expression.receiver = leaf
                        return
                    }
                    substituteLeafExpression(it, leaf)
                }
            }
            is PsExpressionLiteral -> {}
        }
    }

    private fun isConditional(expression: PsExpression): Boolean {
        return expression is PsExpressionOperator
                && expression.operatorSymbol in listOf(OPERATOR_IF_ELSE, OPERATOR_WHEN_WRAPPER, OPERATOR_WHEN_BODY)
    }

    private fun isNullExpression(expression: PsExpression): Boolean {
        return expression is PsExpressionProperty && expression.propertySymbol == Symbol.NULL
    }
}