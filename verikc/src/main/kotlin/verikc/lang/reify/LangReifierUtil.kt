/*
 * Copyright 2020 Francis Wang
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
import verikc.base.ast.ReifiedType
import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpression
import verikc.rf.ast.RfExpressionLiteral

object LangReifierUtil {

    fun toInt(expression: RfExpression): Int {
        val reifiedType = expression.reifiedType
                ?: throw LineException("expression has not been reified", expression.line)
        return if (expression is RfExpressionLiteral
                && reifiedType == ReifiedType(TYPE_INT, INSTANCE, listOf())) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression.line)
    }

    fun matchTypes(leftExpression: RfExpression, rightExpression: RfExpression) {
        val leftReifiedType = leftExpression.reifiedType
                ?: throw LineException("expression has not been reified", leftExpression.line)
        val rightReifiedType = rightExpression.reifiedType
                ?: throw LineException("expression has not been reified", rightExpression.line)
        if (leftReifiedType != rightReifiedType) {
            throw LineException(
                "type mismatch expected $leftReifiedType but got $rightReifiedType",
                leftExpression.line
            )
        }
    }

    fun implicitCast(intExpression: RfExpression, pairedExpression: RfExpression) {
        if (intExpression !is RfExpressionLiteral || intExpression.type != TYPE_INT) {
            throw LineException("failed to cast integer expression", intExpression.line)
        }

        val reifiedType = pairedExpression.reifiedType
                ?: throw LineException("expression has not been reified", pairedExpression.line)
        if (reifiedType.typeSymbol !in listOf(TYPE_UBIT, TYPE_SBIT)) {
            throw LineException("unable to cast integer to $reifiedType", intExpression.line)
        }

        val width = intExpression.value.width - 1
        if (width > reifiedType.args[0]) {
            throw LineException("unable to cast integer of width $width to $reifiedType", intExpression.line)
        }

        intExpression.reifiedType = reifiedType
    }
}
