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

import verikc.base.ast.LineException
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.ps.ast.*

object PsPassConvertConditional: PsPassBase() {

    override fun passModule(module: PsModule) {
        module.actionBlocks.forEach { passBlock(it.block) }
        module.methodBlocks.forEach { passBlock(it.block) }
    }

    override fun passCls(cls: PsCls) {
        cls.methodBlocks.forEach { passBlock(it.block) }
    }

    private fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlock(block) {
            if (it.isSubexpression) convertConditional(it.expression)
            else null
        }
    }

    private fun convertConditional(expression: PsExpression): PsExpression? {
        return if (expression is PsExpressionOperator && expression.operatorSymbol == OPERATOR_IF_ELSE) {
            val ifExpression = blockToSimpleExpression(expression.blocks[0])
            val elseExpression = blockToSimpleExpression(expression.blocks[1])
            if (ifExpression != null && elseExpression != null) {
                PsExpressionFunction(
                    expression.line,
                    expression.typeGenerified,
                    FUNCTION_INTERNAL_IF_ELSE,
                    expression.receiver!!,
                    arrayListOf(ifExpression, elseExpression)
                )
            } else throw LineException("unable to unlift conditional", expression.line)
        } else null
    }

    private fun blockToSimpleExpression(block: PsBlock): PsExpression? {
        return if (block.expressions.size == 1) {
            val expression = block.expressions.last()
            if (expression is PsExpressionOperator && expression.blocks.isNotEmpty()) return null
            else expression
        } else null
    }
}