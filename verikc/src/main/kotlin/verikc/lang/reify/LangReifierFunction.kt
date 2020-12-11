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
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpression
import verikc.rf.ast.RfExpressionFunction
import java.lang.Integer.max

object LangReifierFunction {

    fun reifyClassNativeAddUbit(expression: RfExpressionFunction): TypeReified {
        val leftWidth = getWidth(expression.receiver!!)
        val rightWidth = getWidth(expression.args[0])
        val width = max(leftWidth, rightWidth)
        return TypeReified(TYPE_UBIT, INSTANCE, listOf(width))
    }

    private fun getWidth(expression: RfExpression): Int {
        val typeReified = expression.typeReified
            ?: throw LineException("expression has not been reified", expression.line)
        return typeReified.args[0]
    }
}
