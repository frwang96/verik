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

package verik.core.lang.modules

import verik.core.lang.LangEntryList
import verik.core.lang.LangSymbol.FUNCTION_CON
import verik.core.lang.LangSymbol.OPERATOR_WITH
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_CLASS
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_MODULE
import verik.core.lang.LangSymbol.TYPE_UNIT

object LangModuleCommon: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_instance",
                TYPE_ANY,
                { null },
                TYPE_INSTANCE
        )

        list.addType(
                "_module",
                TYPE_ANY,
                { null },
                TYPE_MODULE
        )

        list.addType(
                "_class",
                TYPE_ANY,
                { null },
                TYPE_CLASS
        )

        list.addFunction(
                "con",
                TYPE_INSTANCE,
                listOf(TYPE_INSTANCE),
                TYPE_UNIT,
                { null },
                { null },
                FUNCTION_CON
        )

        list.addOperator(
                "with",
                { it.blocks[0].lambdaParameters[0].type = it.receiver!!.type
                    TYPE_UNIT },
                { null },
                { null },
                OPERATOR_WITH
        )
    }
}