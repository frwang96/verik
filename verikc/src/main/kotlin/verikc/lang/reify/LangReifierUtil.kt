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
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpression
import verikc.rf.ast.RfExpressionLiteral

object LangReifierUtil {

    fun toInt(expression: RfExpression): Int {
        val typeReified = expression.typeReified
            ?: throw LineException("expression has not been reified", expression.line)
        return if (expression is RfExpressionLiteral
            && typeReified == TypeReified(TYPE_INT, INSTANCE, listOf())
        ) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression.line)
    }

    fun matchTypes(leftExpression: RfExpression, rightExpression: RfExpression) {
        val leftTypeReified = leftExpression.typeReified
            ?: throw LineException("expression has not been reified", leftExpression.line)
        val rightTypeReified = rightExpression.typeReified
            ?: throw LineException("expression has not been reified", rightExpression.line)
        if (leftTypeReified != rightTypeReified) {
            throw LineException(
                "type mismatch expected $leftTypeReified but got $rightTypeReified",
                leftExpression.line
            )
        }
    }

    fun implicitCast(intExpression: RfExpression, pairedExpression: RfExpression) {
        if (intExpression !is RfExpressionLiteral || intExpression.type != TYPE_INT) {
            throw LineException("failed to cast integer expression", intExpression.line)
        }

        val typeReified = pairedExpression.typeReified
            ?: throw LineException("expression has not been reified", pairedExpression.line)
        if (typeReified.typeSymbol !in listOf(TYPE_UBIT, TYPE_SBIT)) {
            throw LineException("unable to cast integer to $typeReified", intExpression.line)
        }

        val width = intExpression.value.width - 1
        if (width > typeReified.args[0]) {
            throw LineException("unable to cast integer of width $width to $typeReified", intExpression.line)
        }

        intExpression.typeReified = typeReified
    }
}
