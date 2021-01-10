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
import verikc.lang.LangSymbol.FUNCTION_CON_DATA_DATA
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY
import verikc.lang.LangSymbol.FUNCTION_TYPE_ANY_ANY
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.lang.LangSymbol.TYPE_ANY
import verikc.lang.LangSymbol.TYPE_CLASS
import verikc.lang.LangSymbol.TYPE_COMPONENT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.sv.ast.SvTypeExtracted

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
            { SvTypeExtracted("void", "", "") },
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
            "type",
            null,
            listOf(TYPE_ANY),
            listOf(TYPE),
            false,
            TYPE_UNIT,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
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
            TYPE_UNIT,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            FUNCTION_TYPE_ANY_ANY
        )

        list.add(
            "con",
            TYPE_DATA,
            listOf(TYPE_DATA),
            listOf(VALUE),
            false,
            TYPE_UNIT,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            FUNCTION_CON_DATA_DATA
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "with",
            TYPE,
            {
                it.expression.blocks[0].lambdaProperties[0].typeSymbol = it.expression.receiver!!.getTypeSymbolNotNull()
                it.expression.receiver.getTypeSymbolNotNull()
            },
            {
                val typeGenerified = it.receiver!!.getTypeGenerifiedNotNull()
                it.blocks[0].lambdaProperties[0].typeGenerified = typeGenerified
                typeGenerified
            },
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
