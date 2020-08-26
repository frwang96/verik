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
import verik.core.it.ItExpression
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItReifiedType
import verik.core.it.ItTypeClass
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_INT

object LangReifierUtil {

    fun toInt(expression: ItExpression): Int {
        val reifiedType = expression.reifiedType
                ?: throw LineException("expression has not been reified", expression)
        return if (expression is ItExpressionLiteral
                && reifiedType == ItReifiedType(TYPE_INT, ItTypeClass.INSTANCE, listOf())) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression)
    }

    fun matchTypes(leftExpression: ItExpression, rightExpression: ItExpression) {
        val leftReifiedType = leftExpression.reifiedType
                ?: throw LineException("expression has not been reified", leftExpression)
        val rightReifiedType = rightExpression.reifiedType
                ?: throw LineException("expression has not been reified", rightExpression)
        if (leftReifiedType != rightReifiedType) {
            throw LineException("type mismatch expected $leftReifiedType but got $rightReifiedType", leftExpression)
        }
    }

    fun implicitCast(intExpression: ItExpression, pairedExpression: ItExpression) {
        if (intExpression !is ItExpressionLiteral || intExpression.type != TYPE_INT) {
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