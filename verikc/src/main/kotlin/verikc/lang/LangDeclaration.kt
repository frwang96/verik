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

package verikc.lang

import verikc.lang.module.*

object LangDeclaration {

    val types: List<LangType>
    val functions: List<LangFunction>
    val operators: List<LangOperator>
    val properties: List<LangProperty>

    private val modules = listOf(
        LangModuleBase,
        LangModuleSpecial,
        LangModuleLoop,
        LangModuleControl,
        LangModuleSystem,
        LangModuleTime,
        LangModuleData,
        LangModuleMisc,
        LangModuleBool,
        LangModuleInt,
        LangModuleString,
        LangModuleUbit,
        LangModuleUbitOp,
        LangModuleSbit,
        LangModuleSbitOp,
        LangModuleList,
        LangModuleArray
    )

    init {
        val typeList = LangTypeList()
        val functionList = LangFunctionList()
        val operatorList = LangOperatorList()
        val propertyList = LangPropertyList()

        modules.forEach {
            it.loadTypes(typeList)
            it.loadFunctions(functionList)
            it.loadOperators(operatorList)
            it.loadProperties(propertyList)
        }

        types = typeList.types
        functions = functionList.functions
        operators = operatorList.operators
        properties = propertyList.properties
    }
}
