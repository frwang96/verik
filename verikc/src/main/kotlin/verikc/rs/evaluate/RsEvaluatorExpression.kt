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

package verikc.rs.evaluate

import verikc.base.ast.LineException
import verikc.lang.LangSymbol.FUNCTION_EXP_INT
import verikc.lang.LangSymbol.FUNCTION_LOG_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_DIV_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_REM_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_INT_INT
import verikc.lang.LangSymbol.TYPE_INT
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
        val receiverEvaluateResult = expression.receiver?.let { evaluate(it, symbolTable) ?: return null }
        val argEvaluateResults = expression.args.map { evaluate(it, symbolTable) ?: return null }

        val value = when (expression.getFunctionSymbolNotNull()) {
            FUNCTION_NATIVE_ADD_INT_INT -> {
                receiverEvaluateResult!!.value + argEvaluateResults[0].value
            }
            FUNCTION_NATIVE_SUB_INT_INT -> {
                receiverEvaluateResult!!.value - argEvaluateResults[0].value
            }
            FUNCTION_NATIVE_MUL_INT_INT -> {
                receiverEvaluateResult!!.value * argEvaluateResults[0].value
            }
            FUNCTION_NATIVE_DIV_INT_INT -> {
                receiverEvaluateResult!!.value / argEvaluateResults[0].value
            }
            FUNCTION_NATIVE_REM_INT_INT -> {
                receiverEvaluateResult!!.value % argEvaluateResults[0].value
            }
            FUNCTION_LOG_INT -> {
                val value = argEvaluateResults[0].value
                if (value <= 0) throw LineException("illegal argument $value to log function", expression.line)
                if (value == 1) 1
                else 32 - (value - 1).countLeadingZeroBits()
            }
            FUNCTION_EXP_INT -> {
                val value = argEvaluateResults[0].value
                if (value < 0 || value >= 31)
                    throw LineException("illegal argument $value to exp function", expression.line)
                1 shl value
            }
            else -> null
        }
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