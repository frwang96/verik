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

package verik.core.lang

import verik.core.lang.modules.LangModuleBase
import verik.core.lang.modules.LangModuleCommon
import verik.core.lang.modules.LangModuleData
import verik.core.lang.modules.LangModuleSystem

object Lang {

    val typeTable = LangTypeTable()
    val functionTable = LangFunctionTable()

    private val modules = listOf(
            LangModuleBase,
            LangModuleCommon,
            LangModuleData,
            LangModuleSystem
    )

    init {
        modules.forEach { it.load(
                typeTable,
                functionTable
        ) }
    }
}