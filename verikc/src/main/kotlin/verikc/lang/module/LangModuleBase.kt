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
import verikc.lang.LangOperatorList
import verikc.lang.LangSymbol.FUNCTION_CON_BUSPORT_BUSPORT
import verikc.lang.LangSymbol.FUNCTION_CON_BUS_BUS
import verikc.lang.LangSymbol.FUNCTION_CON_CLOCKPORT_CLOCKPORT
import verikc.lang.LangSymbol.FUNCTION_CON_DATA_DATA
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY_ANY
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_BUS
import verikc.lang.LangSymbol.TYPE_BUSPORT
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.lang.LangSymbol.TYPE_CLOCKPORT
import verikc.lang.LangSymbol.TYPE_COMPONENT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.lang.resolve.LangResolverFunction
import verikc.sv.ast.SvTypeExtracted

object LangModuleBase: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_any",
            null,
            false,
            { null },
            TYPE_ANY
        )

        list.add(
            "_unit",
            TYPE_ANY,
            false,
            { SvTypeExtracted("void", "", "") },
            TYPE_UNIT
        )

        list.add(
            "_instance",
            TYPE_ANY,
            false,
            { null },
            TYPE_INSTANCE
        )

        list.add(
            "_component",
            TYPE_ANY,
            false,
            { null },
            TYPE_COMPONENT
        )

        list.add(
            "_module",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_MODULE
        )

        list.add(
            "_bus",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_BUS
        )

        list.add(
            "_busport",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_BUSPORT
        )

        list.add(
            "_clockport",
            TYPE_COMPONENT,
            false,
            { null },
            TYPE_CLOCKPORT
        )

        list.add(
            "_class",
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
            "con",
            TYPE_BUS,
            listOf(TYPE_BUS),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveAssign(it) },
            { null },
            FUNCTION_CON_BUS_BUS
        )

        list.add(
            "con",
            TYPE_BUSPORT,
            listOf(TYPE_BUSPORT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveAssign(it) },
            { null },
            FUNCTION_CON_BUSPORT_BUSPORT
        )

        list.add(
            "con",
            TYPE_CLOCKPORT,
            listOf(TYPE_CLOCKPORT),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveAssign(it) },
            { null },
            FUNCTION_CON_CLOCKPORT_CLOCKPORT
        )

        list.add(
            "con",
            TYPE_DATA,
            listOf(TYPE_DATA),
            listOf(VALUE),
            false,
            VALUE,
            { LangResolverFunction.resolveAssign(it) },
            { null },
            FUNCTION_CON_DATA_DATA
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "with",
            TYPE,
            {
                val typeGenerified = it.expression.receiver!!.getTypeGenerifiedNotNull()
                it.expression.blocks[0].lambdaProperties[0].typeGenerified = typeGenerified
                typeGenerified
            },
            { null },
            OPERATOR_WITH
        )
    }
}
