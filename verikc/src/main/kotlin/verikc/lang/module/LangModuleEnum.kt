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

package verikc.lang.module

import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_ENUM_ONE_HOT
import verikc.lang.LangSymbol.FUNCTION_ENUM_SEQUENTIAL
import verikc.lang.LangSymbol.FUNCTION_ENUM_ZERO_ONE_HOT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangTypeList

object LangModuleEnum: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_enum",
            TYPE_DATA,
            { null },
            TYPE_ENUM
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "enum_sequential",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_INT,
            { null },
            { null },
            FUNCTION_ENUM_SEQUENTIAL
        )

        list.add(
            "enum_one_hot",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_INT,
            { null },
            { null },
            FUNCTION_ENUM_ONE_HOT
        )

        list.add(
            "enum_zero_one_hot",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_INT,
            { null },
            { null },
            FUNCTION_ENUM_ZERO_ONE_HOT
        )
    }
}
