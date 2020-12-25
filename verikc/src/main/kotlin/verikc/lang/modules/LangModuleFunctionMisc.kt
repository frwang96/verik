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

package verikc.lang.modules

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_CAT_UBIT_UBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.reify.LangReifierFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionMisc: LangModule {

    override fun load(list: LangEntryList) {
        list.addFunction(
            "cat",
            null,
            listOf(TYPE_UBIT, TYPE_UBIT),
            listOf(INSTANCE, INSTANCE),
            true,
            TYPE_UBIT,
            { LangReifierFunction.reifyCat(it) },
            { SvExpressionOperator(it.function.line, null, SvOperatorType.CONCATENATE, it.args) },
            FUNCTION_CAT_UBIT_UBIT
        )
    }
}
