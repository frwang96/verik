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

package verikc.lang.generify

import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerifiedSimple
import verikc.ge.ast.GeExpressionOperator
import verikc.ge.ast.GeStatementExpression
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT

object LangGenerifierOperator {

    fun generifyIfElse(expression: GeExpressionOperator): TypeGenerifiedSimple {
        val ifStatement = expression.blocks[0].statements.lastOrNull()
        val elseStatement = expression.blocks[1].statements.lastOrNull()
        val ifExpression = if (ifStatement is GeStatementExpression) ifStatement.expression else null
        val elseExpression = if (elseStatement is GeStatementExpression) elseStatement.expression else null
        return when (expression.typeSymbol) {
            TYPE_UBIT, TYPE_SBIT-> {
                if (ifExpression == null || elseExpression == null)
                    throw LineException("unable to generify conditional", expression.line)
                LangGenerifierUtil.inferWidthIfBit(ifExpression, elseExpression)
                LangGenerifierUtil.matchTypes(ifExpression, elseExpression)
                val typeGenerified = ifExpression.typeGenerified!!
                return TypeGenerifiedSimple(typeGenerified.typeSymbol, typeGenerified.args)
            }
            else -> expression.typeSymbol.toTypeGenerified()
        }
    }
}