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

package verik.core.lang.reify

import verik.core.base.LineException
import verik.core.rf.RfExpression
import verik.core.rf.RfExpressionLiteral
import verik.core.rf.RfReifiedType
import verik.core.rf.RfTypeClass
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_INT

object LangReifierUtil {

    fun toInt(expression: RfExpression): Int {
        val reifiedType = expression.reifiedType
                ?: throw LineException("expression has not been reified", expression)
        return if (expression is RfExpressionLiteral
                && reifiedType == RfReifiedType(TYPE_INT, RfTypeClass.INSTANCE, listOf())) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression)
    }

    fun matchTypes(leftExpression: RfExpression, rightExpression: RfExpression) {
        val leftReifiedType = leftExpression.reifiedType
                ?: throw LineException("expression has not been reified", leftExpression)
        val rightReifiedType = rightExpression.reifiedType
                ?: throw LineException("expression has not been reified", rightExpression)
        if (leftReifiedType != rightReifiedType) {
            throw LineException("type mismatch expected $leftReifiedType but got $rightReifiedType", leftExpression)
        }
    }

    fun implicitCast(intExpression: RfExpression, pairedExpression: RfExpression) {
        if (intExpression !is RfExpressionLiteral || intExpression.type != TYPE_INT) {
            throw LineException("failed to cast integer expression", intExpression)
        }

        val reifiedType = pairedExpression.reifiedType
                ?: throw LineException("expression has not been reified", pairedExpression)
        if (reifiedType.type !in listOf(LangSymbol.TYPE_UINT, LangSymbol.TYPE_SINT)) {
            throw LineException("unable to cast integer to $reifiedType", intExpression)
        }

        val size = intExpression.value.size - 1
        if (size > reifiedType.args[0]) {
            throw LineException("unable to cast integer of size $size to $reifiedType", intExpression)
        }

        intExpression.reifiedType = reifiedType
    }
}