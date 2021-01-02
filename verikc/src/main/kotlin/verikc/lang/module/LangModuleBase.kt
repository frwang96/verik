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
import verikc.lang.LangOperatorList
import verikc.lang.LangSymbol.FUNCTION_CON_DATA_DATA
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.lang.LangSymbol.TYPE_COMPONENT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList

object LangModuleBase: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_any",
            null,
            { null },
            TYPE_ANY
        )

        list.add(
            "_unit",
            TYPE_ANY,
            { null },
            TYPE_UNIT
        )

        list.add(
            "_instance",
            TYPE_ANY,
            { null },
            TYPE_INSTANCE
        )

        list.add(
            "_component",
            TYPE_ANY,
            { null },
            TYPE_COMPONENT
        )

        list.add(
            "_module",
            TYPE_COMPONENT,
            { null },
            TYPE_MODULE
        )

        list.add(
            "_class",
            TYPE_INSTANCE,
            { null },
            TYPE_CLASS
        )

    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "con",
            TYPE_DATA,
            listOf(TYPE_DATA),
            listOf(INSTANCE),
            false,
            TYPE_UNIT,
            { null },
            { null },
            FUNCTION_CON_DATA_DATA
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "with",
            {
                it.expression.blocks[0].lambdaProperties[0].typeSymbol = it.expression.receiver!!.getTypeSymbolNotNull()
                TYPE_UNIT
            },
            { null },
            { null },
            OPERATOR_WITH
        )
    }
}
