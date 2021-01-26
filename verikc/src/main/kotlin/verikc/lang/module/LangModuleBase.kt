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
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY_ANY
import verikc.lang.LangSymbol.FUNCTION_WITH_COMPONENT
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_BUS
import verikc.lang.LangSymbol.TYPE_BUS_PORT
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.lang.LangSymbol.TYPE_CLOCK_PORT
import verikc.lang.LangSymbol.TYPE_COMPONENT
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.sv.ast.SvTypeExtracted

object LangModuleBase: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "Any",
            null,
            false,
            { null },
            TYPE_ANY
        )

        list.add(
            "Unit",
            TYPE_ANY,
            false,
            { SvTypeExtracted("void", "", "") },
            TYPE_UNIT
        )

        list.add(
            "Instance",
            TYPE_ANY,
            false,
            { null },
            TYPE_INSTANCE
        )

        list.add(
            "Component",
            TYPE_ANY,
            false,
            { null },
            TYPE_COMPONENT
        )

        list.add(
            "Module",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_MODULE
        )

        list.add(
            "Bus",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_BUS
        )

        list.add(
            "BusPort",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_BUS_PORT
        )

        list.add(
            "ClockPort",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_CLOCK_PORT
        )

        list.add(
            "Class",
            TYPE_INSTANCE,
            false,
            { null },
            TYPE_CLASS
        )

    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "type",
            null,
            listOf(TYPE_ANY),
            listOf(TYPE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            FUNCTION_TYPE_ANY
        )

        list.add(
            "type",
            null,
            listOf(TYPE_ANY, TYPE_ANY),
            listOf(VALUE, TYPE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            FUNCTION_TYPE_ANY_ANY
        )

        list.add(
            "with",
            TYPE_COMPONENT,
            listOf(TYPE_ANY),
            listOf(VALUE),
            true,
            TYPE,
            { it.expression.receiver?.getTypeGenerifiedNotNull() },
            { null },
            FUNCTION_WITH_COMPONENT
        )
    }
}
