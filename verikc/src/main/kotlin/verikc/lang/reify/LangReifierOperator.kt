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

package verikc.lang.reify

import verikc.base.ast.LineException
import verikc.base.ast.TypeReified
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpressionOperator

object LangReifierOperator {

    fun reifyIfElse(expression: RfExpressionOperator): TypeReified {
        val ifExpression = expression.blocks[0].expressions.lastOrNull()
        val elseExpression = expression.blocks[1].expressions.lastOrNull()
        return when (expression.typeSymbol) {
            TYPE_UBIT, TYPE_SBIT-> {
                if (ifExpression == null || elseExpression == null)
                    throw LineException("unable to reify conditional", expression.line)
                LangReifierUtil.inferWidthIfBit(ifExpression, elseExpression)
                LangReifierUtil.matchTypes(ifExpression, elseExpression)
                ifExpression.typeReified!!
            }
            else -> expression.typeSymbol.toTypeReifiedInstance()
        }
    }
}