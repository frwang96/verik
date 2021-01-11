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
import verikc.lang.LangOperatorList
import verikc.lang.LangSymbol.FUNCTION_RANGE_INT
import verikc.lang.LangSymbol.OPERATOR_FOR
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_LIST
import verikc.lang.LangSymbol.TYPE_UNIT

object LangModuleLoop: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "range",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_LIST.toTypeGenerified(TYPE_INT.toTypeGenerified()) },
            { null },
            FUNCTION_RANGE_INT
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "for",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            OPERATOR_FOR
        )
    }
}
