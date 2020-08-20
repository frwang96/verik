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

import verik.core.base.LineException
import verik.core.it.ItExpression
import verik.core.it.ItExpressionLiteral
import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangSymbol.TYPE_INT

object LangFunctionReifierUtil {

    fun toInt(expression: ItExpression): Int {
        val typeReified = expression.typeReified
                ?: throw LineException("expression has not been reified", expression)
        return if (expression is ItExpressionLiteral
                && typeReified == ItTypeReified(TYPE_INT, ItTypeClass.INSTANCE, listOf())) {
            expression.value.toInt()
        } else throw LineException("expected int literal", expression)
    }
}