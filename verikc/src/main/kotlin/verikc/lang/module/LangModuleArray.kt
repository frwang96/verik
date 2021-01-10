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

import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangTypeList
import verikc.lang.generify.LangGenerifierUtil

object LangModuleArray: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_array",
            TYPE_INSTANCE,
            { null },
            TYPE_ARRAY
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_array",
            null,
            listOf(TYPE_INT, TYPE_INSTANCE),
            listOf(VALUE, TYPE),
            false,
            TYPE_ARRAY,
            TYPE,
            {
                TYPE_ARRAY.toTypeGenerified(
                    LangGenerifierUtil.intLiteralToInt(it.args[0]),
                    it.args[1].getTypeGenerifiedNotNull()
                )
            },
            { null },
            FUNCTION_TYPE_ARRAY
        )
    }
}
