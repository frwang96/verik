/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.lang.module

import verikc.base.ast.ExpressionClass.VALUE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_RANDOM
import verikc.lang.LangSymbol.FUNCTION_RANDOM_INT
import verikc.lang.LangSymbol.TYPE_INT
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleRandom: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "random",
            null,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_INT.toTypeGenerified() },
            { SvExpressionFunction(it.expression.line, null, "\$urandom", listOf()) },
            FUNCTION_RANDOM
        )

        list.add(
            "random",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_INT.toTypeGenerified() },
            {
                SvExpressionFunction(
                    it.expression.line,
                    null,
                    "\$urandom_range",
                    listOf(
                        SvExpressionOperator(
                            it.expression.line,
                            it.args[0],
                            SvOperatorType.SUB,
                            listOf(SvExpressionLiteral(it.expression.line, "1"))
                        )
                    )
                )
            },
            FUNCTION_RANDOM_INT
        )
    }
}
