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

    fun replaceBlockExpression(block: PsBlock, replacer: (PsExpression) -> List<PsExpression>?) {
        var index = 0
        while (index < block.expressions.size) {
            val replacement = replacer(block.expressions[index])
            if (replacement != null) {
                block.expressions.removeAt(index)
                replacement.reversed().forEach { block.expressions.add(index, it) }
                index += replacement.size
            } else {
                index++
            }
        }
        block.expressions.forEach {
            if (it is PsExpressionOperator) {
                it.blocks.forEach { block -> replaceBlockExpression(block, replacer) }
            }
        }
    }

    fun replaceBlockSubexpression(block: PsBlock, replacer: (PsExpression) -> PsExpression?) {
        block.expressions.forEachIndexed { index, expression ->
            replaceSubexpression(block.expressions[index], replacer)
            replacer(expression)?.let { block.expressions[index] = it }
        }
    }

    private fun replaceSubexpression(expression: PsExpression, replacer: (PsExpression) -> PsExpression?) {
        when (expression) {
            is PsExpressionFunction -> {
                @Suppress("DuplicatedCode")
                expression.receiver?.also { receiver ->
                    replaceSubexpression(expression.receiver!!, replacer)
                    replacer(receiver)?.let { expression.receiver = it }
                }
                expression.args.forEachIndexed {  index, arg ->
                    replaceSubexpression(expression.args[index], replacer)
                    replacer(arg)?.let { expression.args[index] = it }
                }
            }
            is PsExpressionOperator -> {
                expression.receiver?.also { receiver ->
                    replaceSubexpression(expression.receiver!!, replacer)
                    replacer(receiver)?.let { expression.receiver = it }
                }
                expression.args.forEachIndexed {  index, arg ->
                    replaceSubexpression(expression.args[index], replacer)
                    replacer(arg)?.let { expression.args[index] = it }
                }
                expression.blocks.forEach {
                    replaceBlockSubexpression(it, replacer)
                }
            }
            is PsExpressionProperty -> {
                expression.receiver?.also { receiver ->
                    replaceSubexpression(expression.receiver!!, replacer)
                    replacer(receiver)?.let { expression.receiver = it }
                }
            }
            is PsExpressionLiteral -> {}
            is PsExpressionType -> {}
        }
    }
}