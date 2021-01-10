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
import verikc.lang.LangSymbol.FUNCTION_NATIVE_AND_BOOL_BOOL
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_BOOL
import verikc.lang.LangSymbol.FUNCTION_NATIVE_OR_BOOL_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangTypeList
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleBool: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_bool",
            TYPE_LOGIC,
            { SvTypeExtracted("logic", "", "") },
            TYPE_BOOL
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_bool",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_BOOL,
            TYPE,
            { TYPE_BOOL.toTypeGenerified() },
            { TYPE_BOOL.toTypeGenerified() },
            { null },
            FUNCTION_TYPE_BOOL
        )

        list.add(
            "!",
            TYPE_BOOL,
            listOf(),
            listOf(),
            false,
            TYPE_BOOL,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_NEGATION, listOf()) },
            FUNCTION_NATIVE_NOT_BOOL
        )

        list.add(
            "&&",
            TYPE_BOOL,
            listOf(TYPE_BOOL),
            listOf(VALUE),
            false,
            TYPE_BOOL,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_AND, it.args) },
            FUNCTION_NATIVE_AND_BOOL_BOOL
        )

        list.add(
            "||",
            TYPE_BOOL,
            listOf(TYPE_BOOL),
            listOf(VALUE),
            false,
            TYPE_BOOL,
            VALUE,
            { TYPE_BOOL.toTypeGenerified() },
            { TYPE_BOOL.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.LOGICAL_OR, it.args) },
            FUNCTION_NATIVE_OR_BOOL_BOOL
        )
    }
}