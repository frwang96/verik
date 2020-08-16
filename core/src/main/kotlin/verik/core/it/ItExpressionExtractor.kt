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

package verik.core.it

import verik.core.lang.Lang
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.main.LineException
import verik.core.sv.SvExpression
import verik.core.sv.SvExpressionFunction

object ItExpressionExtractor {

    fun extract(expression: ItExpression): SvExpression {
        return when(expression) {
            is ItExpressionFunction -> extractFunction(expression)
            is ItExpressionOperator -> throw LineException("extraction of operator expressions is not supported", expression)
            is ItExpressionProperty -> throw LineException("extraction of property expressions is not supported", expression)
            is ItExpressionString -> throw LineException("extraction of string expressions is not supported", expression)
            is ItExpressionLiteral -> throw LineException("extraction of literal expressions is not supported", expression)
        }
    }

    private fun extractFunction(function: ItExpressionFunction): SvExpressionFunction {
        val target = function.target?.let { extract(it) }
        val args = function.args.map { extract(it) }
        return Lang.functionTable.extract(LangFunctionExtractorRequest(
                function,
                target,
                args
        ))
    }
}