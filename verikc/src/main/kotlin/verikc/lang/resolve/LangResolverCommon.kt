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

package verikc.lang.resolve

import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.ast.RsExpression
import verikc.rs.ast.RsExpressionOperator
import verikc.rs.ast.RsStatementExpression
import verikc.rs.resolve.RsEvaluatorExpression
import verikc.rs.table.RsSymbolTable

object LangResolverCommon {

    fun evaluateToInt(expression: RsExpression, symbolTable: RsSymbolTable): Int {
        val evaluateResult = RsEvaluatorExpression.evaluate(expression, symbolTable)
        return evaluateResult?.value
            ?: throw LineException("unable to evaluate expression", expression.line)
    }

    fun bitToWidth(expression: RsExpression): Int {
        val typeGenerified = expression.getTypeGenerifiedNotNull()
        return when (typeGenerified.typeSymbol) {
            TYPE_UBIT, TYPE_SBIT -> typeGenerified.getInt(0)
            else -> throw LineException("expected bit type", expression.line)
        }
    }

    fun inferWidthIfBit(leftExpression: RsExpression, rightExpression: RsExpression) {
        val leftTypeGenerified = leftExpression.getTypeGenerifiedNotNull()
        val rightTypeGenerified = rightExpression.getTypeGenerifiedNotNull()
        if (leftTypeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
            && rightTypeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
        ) {
            val leftWidth = leftTypeGenerified.getInt(0)
            val rightWidth = rightTypeGenerified.getInt(0)
            when {
                leftWidth == 0 && rightWidth != 0 ->
                    setWidth(leftExpression, rightWidth)
                leftWidth != 0 && rightWidth == 0 ->
                    setWidth(rightExpression, leftWidth)
            }
        }
    }

    fun inferWidthIfBit(expression: RsExpression, typeGenerified: TypeGenerified) {
        val expressionTypeGenerified = expression.getTypeGenerifiedNotNull()
        if (typeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
            && expressionTypeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
            && expressionTypeGenerified.getInt(0) == 0
        ) {
            setWidth(expression, typeGenerified.getInt(0))
        }
    }

    fun matchTypes(leftExpression: RsExpression, rightExpression: RsExpression) {
        val leftTypeGenerified = leftExpression.getTypeGenerifiedNotNull()
        val rightTypeGenerified = rightExpression.getTypeGenerifiedNotNull()
        if (leftTypeGenerified != rightTypeGenerified) {
            throw LineException(
                "type mismatch expected $leftTypeGenerified but got $rightTypeGenerified",
                leftExpression.line
            )
        }
    }

    fun matchTypes(expression: RsExpression, typeGenerified: TypeGenerified) {
        val expressionTypeGenerified = expression.getTypeGenerifiedNotNull()
        if (expressionTypeGenerified != typeGenerified) {
            throw LineException(
                "type mismatch expected $typeGenerified but got $expressionTypeGenerified",
                expression.line
            )
        }
    }

    fun matchWidth(leftExpression: RsExpression, rightExpression: RsExpression) {
        val leftWidth = bitToWidth(leftExpression)
        val rightWidth = bitToWidth(rightExpression)
        if (leftWidth != rightWidth)
            throw LineException("width mismatch expected $leftWidth but got $rightWidth", leftExpression.line)
    }

    private fun setWidth(expression: RsExpression, width: Int) {
        expression.typeGenerified = expression.typeGenerified!!.typeSymbol.toTypeGenerified(width)
        if (expression is RsExpressionOperator && expression.operatorSymbol == OPERATOR_IF_ELSE) {
            expression.blocks[0].statements.lastOrNull()?.let {
                if (it is RsStatementExpression) setWidth(it.expression, width)
            }
            expression.blocks[1].statements.lastOrNull()?.let {
                if (it is RsStatementExpression) setWidth(it.expression, width)
            }
        }
    }
}
