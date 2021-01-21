/*
 * Copyright (c) 2021 Francis Wang
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
import verikc.lang.LangSymbol.FUNCTION_NATIVE_AND_BOOLEAN_BOOLEAN
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_BOOLEAN
import verikc.lang.LangSymbol.FUNCTION_NATIVE_OR_BOOLEAN_BOOLEAN
import verikc.lang.LangSymbol.FUNCTION_T_BOOLEAN
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangTypeList
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleBool: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "Boolean",
            TYPE_LOGIC,
            false,
            { SvTypeExtracted("logic", "", "") },
            TYPE_BOOLEAN
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "t_Boolean",
            null,
            listOf(),
            listOf(),
            false,
            TYPE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { null },
            FUNCTION_T_BOOLEAN
        )

        list.add(
            "!",
            TYPE_BOOLEAN,
            listOf(),
            listOf(),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_NEGATION, listOf()) },
            FUNCTION_NATIVE_NOT_BOOLEAN
        )

        list.add(
            "&&",
            TYPE_BOOLEAN,
            listOf(TYPE_BOOLEAN),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_AND, it.args) },
            FUNCTION_NATIVE_AND_BOOLEAN_BOOLEAN
        )

        list.add(
            "||",
            TYPE_BOOLEAN,
            listOf(TYPE_BOOLEAN),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_BOOLEAN.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_OR, it.args) },
            FUNCTION_NATIVE_OR_BOOLEAN_BOOLEAN
        )
    }
}