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

package verik.core.it.reify

import verik.core.it.*
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.main.LineException

object ItExpressionReifier {

    fun reifyFile(file: ItFile) {}

    fun reifyExpression(expression: ItExpression) {
        when (expression) {
            is ItExpressionFunction -> reifyFunction(expression)
            is ItExpressionOperator -> throw LineException("reification of function expression not supported", expression)
            is ItExpressionProperty -> throw LineException("reification of function expression not supported", expression)
            is ItExpressionString -> throw LineException("reification of function expression not supported", expression)
            is ItExpressionLiteral -> reifyLiteral(expression)
        }
        if (expression.typeReified == null) {
            throw LineException("could not reify expression", expression)
        }
    }

    private fun reifyFunction(expression: ItExpressionFunction) {
        Lang.functionTable.reify(expression)
    }

    private fun reifyLiteral(expression: ItExpressionLiteral) {
        expression.typeReified = when (expression.type) {
            TYPE_BOOL -> ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf())
            TYPE_INT -> ItTypeReified(TYPE_INT, ItTypeClass.INT, listOf())
            else -> throw LineException("bool or Int type expected", expression)
        }
    }
}