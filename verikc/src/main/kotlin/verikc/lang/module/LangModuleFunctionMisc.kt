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

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_CAT_INSTANCE_INSTANCE_VARARG
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.reify.LangReifierFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionMisc: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "cat",
            null,
            listOf(TYPE_INSTANCE, TYPE_INSTANCE),
            listOf(INSTANCE, INSTANCE),
            true,
            TYPE_UBIT,
            { LangReifierFunction.reifyCat(it) },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.CONCATENATE, it.args) },
            FUNCTION_CAT_INSTANCE_INSTANCE_VARARG
        )
    }
}
