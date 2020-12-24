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
import verikc.lang.LangSymbol.FUNCTION_CON
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.lang.LangSymbol.TYPE_COMPONENT
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.lang.LangSymbol.TYPE_UNIT

object LangModuleBase: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
            "_unit",
            null,
            { null },
            TYPE_UNIT
        )

        list.addType(
            "_any",
            null,
            { null },
            TYPE_ANY
        )

        list.addType(
            "_instance",
            TYPE_ANY,
            { null },
            TYPE_INSTANCE
        )

        list.addType(
            "_component",
            TYPE_ANY,
            { null },
            TYPE_COMPONENT
        )

        list.addType(
            "_module",
            TYPE_COMPONENT,
            { null },
            TYPE_MODULE
        )

        list.addType(
            "_class",
            TYPE_INSTANCE,
            { null },
            TYPE_CLASS
        )

        list.addFunction(
            "con",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            TYPE_UNIT,
            { null },
            { null },
            FUNCTION_CON
        )

        list.addOperator(
            "with",
            {
                it.blocks[0].lambdaProperties[0].typeSymbol = it.receiver!!.getTypeSymbolNotNull()
                TYPE_UNIT
            },
            { null },
            { null },
            OPERATOR_WITH
        )
    }
}
