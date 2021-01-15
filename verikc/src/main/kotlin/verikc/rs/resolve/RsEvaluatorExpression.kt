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

package verikc.rs.resolve

import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.resolve.LangEvaluator
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsEvaluatorExpression {

    fun evaluate(expression: RsExpression, symbolTable: RsSymbolTable): RsEvaluateResult? {
        return when (expression) {
            is RsExpressionFunction -> evaluateFunction(expression, symbolTable)
            is RsExpressionOperator -> null
            is RsExpressionProperty -> evaluateProperty(expression, symbolTable)
            is RsExpressionString -> null
            is RsExpressionLiteral -> evaluateLiteral(expression)
        }
    }

    private fun evaluateFunction(
        expression: RsExpressionFunction,
        symbolTable: RsSymbolTable
    ): RsEvaluateResult? {
        val receiverEvaluated = expression.receiver
            ?.let { evaluate(it, symbolTable) ?: return null }
            .let { it?.value }
        val argsEvaluated = expression.args
            .map { evaluate(it, symbolTable) ?: return null }
            .map { it.value }

        val value = LangEvaluator.evaluate(
            expression.getFunctionSymbolNotNull(),
            receiverEvaluated,
            argsEvaluated,
            expression.line
        )

        return value?.let { RsEvaluateResult(it) }
    }

    private fun evaluateProperty(expression: RsExpressionProperty, symbolTable: RsSymbolTable): RsEvaluateResult? {
        return symbolTable.getPropertyEvaluateResult(expression.getPropertySymbolNotNull(), expression.line)
    }

    private fun evaluateLiteral(expression: RsExpressionLiteral): RsEvaluateResult? {
        return if (expression.getTypeGenerifiedNotNull().typeSymbol == TYPE_INT) {
            RsEvaluateResult(expression.getValueNotNull().toInt())
        } else null
    }
}