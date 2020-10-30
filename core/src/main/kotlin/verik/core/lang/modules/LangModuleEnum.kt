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
import verik.core.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verik.core.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verik.core.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verik.core.lang.LangSymbol.TYPE_ANY
import verik.core.lang.LangSymbol.TYPE_ENUM
import verik.core.lang.LangSymbol.TYPE_INT

object LangModuleEnum: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_enum",
                TYPE_ANY,
                { null },
                TYPE_ENUM
        )

        list.addFunction(
                "enum_sequential",
                null,
                listOf(),
                listOf(),
                TYPE_INT,
                { null },
                { null },
                FUNCTION_ENUM_SEQUENTIAL
        )

        list.addFunction(
                "enum_one_hot",
                null,
                listOf(),
                listOf(),
                TYPE_INT,
                { null },
                { null },
                FUNCTION_ENUM_ONE_HOT
        )

        list.addFunction(
                "enum_zero_one_hot",
                null,
                listOf(),
                listOf(),
                TYPE_INT,
                { null },
                { null },
                FUNCTION_ENUM_ZERO_ONE_HOT
        )
    }
}