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

import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.ps.ast.*

object PsPassConvertConditional: PsPassBase() {

    override fun passBlock(block: PsBlock) {
        PsPassUtil.replaceBlockSubexpression(block) {
            if (it is PsExpressionOperator && it.operatorSymbol == OPERATOR_IF_ELSE) {
                convertConditional(it)
            } else null
        }
    }

    private fun convertConditional(expression: PsExpressionOperator): PsExpression? {
        val ifExpression = blockToSimpleExpression(expression.blocks[0])
        val elseExpression = blockToSimpleExpression(expression.blocks[1])
        return if (ifExpression != null && elseExpression != null) {
            PsExpressionFunction(
                expression.line,
                expression.typeGenerified,
                FUNCTION_INTERNAL_IF_ELSE,
                expression.receiver!!,
                arrayListOf(ifExpression, elseExpression)
            )
        } else null
    }

    private fun blockToSimpleExpression(block: PsBlock): PsExpression? {
        return if (block.expressions.size == 1) {
            val expression = block.expressions.last()
            if (expression is PsExpressionOperator && expression.operatorSymbol == OPERATOR_IF_ELSE) {
                convertConditional(expression)
            } else if (isSimpleExpression(expression)) {
                expression
            } else null
        } else null
    }

    private fun  isSimpleExpression(expression: PsExpression): Boolean {
        return when (expression) {
            is PsExpressionFunction -> expression.functionSymbol != LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
            is PsExpressionLiteral -> true
            is PsExpressionOperator -> false
            is PsExpressionProperty -> true
            is PsExpressionType -> false
        }
    }
}