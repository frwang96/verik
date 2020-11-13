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

package verikc.lang

import verikc.lang.modules.*

object Lang {

    val types: List<LangType>
    val functions: List<LangFunction>
    val operators: List<LangOperator>
    val properties: List<LangProperty>

    private val modules = listOf(
            LangModuleBase,
            LangModuleCommon,
            LangModuleEnum,
            LangModuleProperty,
            LangModuleControl,
            LangModuleAssignment,
            LangModuleData,
            LangModuleFunctionsNative,
            LangModuleString,
            LangModuleSystem
    )

    init {
        val list = LangEntryList()
        modules.forEach {
            it.load(list)
        }

        types = list.types
        functions = list.functions
        operators = list.operators
        properties = list.properties
    }
}
