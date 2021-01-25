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

import verikc.ps.ast.*

object PsPassUtil {

    fun replaceBlock(block: PsBlock, replacer: (PsReplacerRequest) -> PsExpression?) {
        block.expressions.forEachIndexed { index, expression ->
            replaceExpression(block.expressions[index], replacer)
            replacer(PsReplacerRequest(expression, false))?.let { block.expressions[index] = it }
        }
    }

    private fun replaceExpression(expression: PsExpression, replacer: (PsReplacerRequest) -> PsExpression?) {
        when (expression) {
            is PsExpressionFunction -> {
                @Suppress("DuplicatedCode")
                expression.receiver?.let { receiver ->
                    replaceExpression(expression.receiver!!, replacer)
                    replacer(PsReplacerRequest(receiver, true))?.let { expression.receiver = it }
                }
                expression.args.forEachIndexed {  index, arg ->
                    replaceExpression(expression.args[index], replacer)
                    replacer(PsReplacerRequest(arg, true))?.let { expression.args[index] = it }
                }
            }
            is PsExpressionOperator -> {
                expression.receiver?.let { receiver ->
                    replaceExpression(expression.receiver!!, replacer)
                    replacer(PsReplacerRequest(receiver, true))?.let { expression.receiver = it }
                }
                expression.args.forEachIndexed {  index, arg ->
                    replaceExpression(expression.args[index], replacer)
                    replacer(PsReplacerRequest(arg, true))?.let { expression.args[index] = it }
                }
                expression.blocks.forEach {
                    replaceBlock(it, replacer)
                }
            }
            is PsExpressionProperty -> {
                expression.receiver?.let { receiver ->
                    replaceExpression(expression.receiver!!, replacer)
                    replacer(PsReplacerRequest(receiver, true))?.let { expression.receiver = it }
                }
            }
            is PsExpressionLiteral -> {}
        }
    }
}