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

package verikc.lang.generify

import verikc.base.ast.LineException
import verikc.ge.ast.GeExpression
import verikc.ge.ast.GeExpressionLiteral
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT

object LangGenerifierUtil {

    fun intLiteralToInt(expression: GeExpression): Int {
        val typeGenerified = expression.getTypeGenerifiedNotNull()
        return if (expression is GeExpressionLiteral && typeGenerified == TYPE_INT.toTypeGenerifiedInstance()) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression.line)
    }

    fun bitToWidth(expression: GeExpression): Int {
        val typeGenerified = expression.getTypeGenerifiedNotNull()
        return when (typeGenerified.typeSymbol) {
            TYPE_UBIT, TYPE_SBIT -> typeGenerified.args[0]
            else -> throw LineException("expected bit type", expression.line)
        }
    }

    fun inferWidthIfBit(leftExpression: GeExpression, rightExpression: GeExpression) {
        val leftTypeGenerified = leftExpression.getTypeGenerifiedNotNull()
        val rightTypeGenerified = rightExpression.getTypeGenerifiedNotNull()
        if (leftTypeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
            && rightTypeGenerified.typeSymbol in listOf(TYPE_UBIT, TYPE_SBIT)
        ) {
            val leftWidth = leftTypeGenerified.args[0]
            val rightWidth = rightTypeGenerified.args[0]
            when {
                leftWidth == 0 && rightWidth != 0 ->
                    leftExpression.typeGenerified = leftTypeGenerified.typeSymbol.toTypeGenerifiedInstance(rightWidth)
                leftWidth != 0 && rightWidth == 0 ->
                    rightExpression.typeGenerified = rightTypeGenerified.typeSymbol.toTypeGenerifiedInstance(leftWidth)
                leftWidth == 0 && rightWidth == 0 ->
                    throw LineException("could not infer width of bit type", leftExpression.line)
            }
        }
    }

    fun matchTypes(leftExpression: GeExpression, rightExpression: GeExpression) {
        val leftTypeGenerified = leftExpression.getTypeGenerifiedNotNull()
        val rightTypeGenerified = rightExpression.getTypeGenerifiedNotNull()
        if (leftTypeGenerified != rightTypeGenerified) {
            throw LineException(
                "type mismatch expected $leftTypeGenerified but got $rightTypeGenerified",
                leftExpression.line
            )
        }
    }

    fun matchWidth(leftExpression: GeExpression, rightExpression: GeExpression) {
        val leftWidth = bitToWidth(leftExpression)
        val rightWidth = bitToWidth(rightExpression)
        if (leftWidth != rightWidth)
            throw LineException("width mismatch expected $leftWidth but got $rightWidth", leftExpression.line)
    }
}
