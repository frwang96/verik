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

import verik.core.base.ast.LineException
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.rf.ast.RfExpression
import verik.core.rf.ast.RfExpressionFunction
import verik.core.rf.ast.RfReifiedType
import verik.core.rf.ast.RfTypeClass
import java.lang.Integer.max

object LangReifierFunction {

    fun reifyClassNativeAddUint(expression: RfExpressionFunction): RfReifiedType {
        val leftWidth = getWidth(expression.receiver!!)
        val rightWidth = getWidth(expression.args[0])
        val width = max(leftWidth, rightWidth)
        return RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(width))
    }

    private fun getWidth(expression: RfExpression): Int {
        val reifiedType = expression.reifiedType
                ?: throw LineException("expression has not been reified", expression)
        return reifiedType.args[0]
    }
}