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
import verikc.lang.LangSymbol.FUNCTION_FATAL
import verikc.lang.LangSymbol.FUNCTION_FINISH
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.sv.ast.SvExpressionFunction

object LangModuleSystem: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "finish",
            null,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionFunction(it.expression.line, null, "\$finish", listOf()) },
            FUNCTION_FINISH
        )

        list.add(
            "fatal",
            null,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionFunction(it.expression.line, null, "\$fatal", listOf()) },
            FUNCTION_FATAL
        )
    }
}
