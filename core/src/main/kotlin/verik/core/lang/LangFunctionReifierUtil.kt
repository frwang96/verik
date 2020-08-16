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

package verik.core.lang

import verik.core.it.ItExpression
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeReified
import verik.core.main.LineException

object LangFunctionReifierUtil {

    fun instantiate(
            request: LangFunctionReifierRequest,
            typeReified: ItTypeReified
    ): ItExpressionFunction {
        return ItExpressionFunction(
                request.function.line,
                typeReified.type,
                typeReified,
                request.function.function,
                request.target,
                request.args
        )
    }

    fun toInt(expression: ItExpression): Int {
        return if (expression is ItExpressionLiteral && expression.typeReified!!.type == LangSymbol.TYPE_INT) {
            expression.value.toInt()
        } else throw LineException("expected integer literal", expression)
    }
}