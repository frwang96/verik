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
import verikc.lang.LangSymbol.FUNCTION_CAT_DATA_DATA_VARARG
import verikc.lang.LangSymbol.FUNCTION_EXP_INT
import verikc.lang.LangSymbol.FUNCTION_LOG_INT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.resolve.LangResolverFunction
import verikc.sv.ast.SvExpressionFunction
import verikc.sv.ast.SvExpressionLiteral
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleMisc: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "cat",
            null,
            listOf(TYPE_DATA, TYPE_DATA),
            listOf(VALUE, VALUE),
            true,
            VALUE,
            { LangResolverFunction.resolveCat(it) },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.CONCATENATE, it.args) },
            FUNCTION_CAT_DATA_DATA_VARARG
        )

        list.add(
            "log",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_INT.toTypeGenerified() },
            { SvExpressionFunction(it.expression.line, null, "\$clog2", it.args) },
            FUNCTION_LOG_INT
        )

        list.add(
            "exp",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_INT.toTypeGenerified() },
            {
                SvExpressionOperator(
                    it.expression.line,
                    SvExpressionLiteral(it.expression.line, "1"),
                    SvOperatorType.SLL,
                    it.args
                )
            },
            FUNCTION_EXP_INT
        )
    }
}
