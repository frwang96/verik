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

import verikc.base.ast.TypeClass
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_INT_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_INT
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangTypeList
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType
import verikc.sv.ast.SvTypeExtracted

object LangModuleInt: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_int",
            TYPE_DATA,
            { SvTypeExtracted("integer", "", "") },
            TYPE_INT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "_int",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedType() },
            { null },
            FUNCTION_TYPE_INT
        )

        list.add(
            "+",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_INT_INT
        )

        list.add(
            "-",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_INT_INT
        )

        list.add(
            "*",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(TypeClass.INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_INT_INT
        )
    }
}