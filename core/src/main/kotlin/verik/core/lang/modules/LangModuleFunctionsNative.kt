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

package verik.core.lang.modules

import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.*
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_UINT
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_ADD_UINT_INT
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_ADD_UINT_UINT
import verik.core.lang.LangSymbol.FUNCTION_NATIVE_NOT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.reify.LangReifierFunction
import verik.core.lang.reify.LangReifierUtil
import verik.core.sv.SvOperatorType
import verik.core.sv.SvStatementExpression

object LangModuleFunctionsNative: LangModule {

    private val extractorNativeAdd = { request: LangFunctionExtractorRequest ->
        SvStatementExpression.wrapOperator(
                request.function.line,
                request.target,
                SvOperatorType.ADD,
                request.args
        )
    }

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable,
            operatorTable: LangOperatorTable
    ) {
        functionTable.add(LangFunction(
                "!",
                TYPE_BOOL,
                listOf(),
                TYPE_BOOL,
                { it.typeReified = ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()) },
                { SvStatementExpression.wrapOperator(
                        it.function.line,
                        it.target,
                        SvOperatorType.NOT,
                        listOf()
                ) },
                FUNCTION_NATIVE_NOT
        ))

        functionTable.add(LangFunction(
                "+",
                TYPE_INT,
                listOf(TYPE_INT),
                TYPE_INT,
                { it.typeReified = ItTypeReified(TYPE_INT, ItTypeClass.INSTANCE, listOf()) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_INT_INT
        ))

        functionTable.add(LangFunction(
                "+",
                TYPE_INT,
                listOf(TYPE_UINT),
                TYPE_UINT,
                { LangReifierUtil.implicitCast(it.target!!, it.args[0])
                    it.typeReified = LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_INT_UINT
        ))

        functionTable.add(LangFunction(
                "+",
                TYPE_UINT,
                listOf(TYPE_INT),
                TYPE_UINT,
                { LangReifierUtil.implicitCast(it.args[0], it.target!!)
                    it.typeReified = LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_UINT_INT
        ))

        functionTable.add(LangFunction(
                "+",
                TYPE_UINT,
                listOf(TYPE_UINT),
                TYPE_UINT,
                { it.typeReified = LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_UINT_UINT
        ))
    }
}