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

package verikc.lang.reify

import verikc.base.ast.LineException
import verikc.lang.BitType
import verikc.lang.LangSymbol.TYPE_INT
import verikc.rf.ast.RfExpression
import verikc.rf.ast.RfExpressionLiteral

object LangReifierUtil {

    fun intLiteralToInt(expression: RfExpression): Int {
        val typeReified = expression.getTypeReifiedNotNull()
        return if (expression is RfExpressionLiteral && typeReified == TYPE_INT.toTypeReifiedInstance()) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression.line)
    }

    fun bitToWidth(expression: RfExpression, bitType: BitType): Int {
        val typeReified = expression.getTypeReifiedNotNull()
        return if (typeReified.typeSymbol == bitType.symbol()) typeReified.args[0]
        else throw LineException("expected $bitType", expression.line)
    }

    fun inferWidth(leftExpression: RfExpression, rightExpression: RfExpression, bitType: BitType) {
        val leftTypeReified = leftExpression.getTypeReifiedNotNull()
        val rightTypeReified = rightExpression.getTypeReifiedNotNull()
        if (leftTypeReified.typeSymbol == bitType.symbol() && rightTypeReified.typeSymbol == bitType.symbol()) {
            val leftWidth = leftTypeReified.args[0]
            val rightWidth = rightTypeReified.args[0]
            when {
                leftWidth == 0 && rightWidth != 0 -> leftExpression.typeReified = rightTypeReified
                leftWidth != 0 && rightWidth == 0 -> rightExpression.typeReified = leftTypeReified
                leftWidth == 0 && rightWidth == 0 ->
                    throw LineException("could not infer width of $bitType", leftExpression.line)
            }
        }
    }

    fun matchTypes(leftExpression: RfExpression, rightExpression: RfExpression) {
        val leftTypeReified = leftExpression.getTypeReifiedNotNull()
        val rightTypeReified = rightExpression.getTypeReifiedNotNull()
        if (leftTypeReified != rightTypeReified) {
            throw LineException(
                "type mismatch expected $leftTypeReified but got $rightTypeReified",
                leftExpression.line
            )
        }
    }
}
