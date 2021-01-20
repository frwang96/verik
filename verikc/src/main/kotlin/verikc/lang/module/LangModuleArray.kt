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
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_ARRAY_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_ARRAY_UBIT
import verikc.lang.LangSymbol.FUNCTION_TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_ARRAY
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangTypeList
import verikc.lang.resolve.LangResolverCommon
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleArray: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_array",
            TYPE_INSTANCE,
            true,
            {
                val typeExtracted = it.typesExtracted[1]!!
                SvTypeExtracted(
                    typeExtracted.identifier,
                    typeExtracted.packed,
                    "${typeExtracted.unpacked}[${it.typeGenerified.getInt(0)}]"
                )
            },
            TYPE_ARRAY
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_array",
            null,
            listOf(TYPE_INT, TYPE_INSTANCE),
            listOf(VALUE, TYPE),
            false,
            TYPE,
            {
                TYPE_ARRAY.toTypeGenerified(
                    LangResolverCommon.evaluateToInt(it.expression.args[0], it.symbolTable),
                    it.expression.args[1].getTypeGenerifiedNotNull()
                )
            },
            { null },
            FUNCTION_TYPE_ARRAY
        )

        list.add(
            "get",
            TYPE_ARRAY,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.getTypeGenerifiedNotNull().getType(1) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_ARRAY_INT
        )

        list.add(
            "get",
            TYPE_ARRAY,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            VALUE,
            { it.expression.receiver!!.getTypeGenerifiedNotNull().getType(1) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_ARRAY_UBIT
        )
    }
}
