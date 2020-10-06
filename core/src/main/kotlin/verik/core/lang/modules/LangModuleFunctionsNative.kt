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

import verik.core.rf.RfReifiedType
import verik.core.rf.RfTypeClass
import verik.core.rf.symbol.RfFunctionExtractorRequest
import verik.core.lang.LangEntryList
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

    private val extractorNativeAdd = { request: RfFunctionExtractorRequest ->
        SvStatementExpression.wrapOperator(
                request.function.line,
                request.receiver,
                SvOperatorType.ADD,
                request.args
        )
    }

    override fun load(list: LangEntryList) {
        list.addFunction(
                "!",
                TYPE_BOOL,
                listOf(),
                TYPE_BOOL,
                { RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()) },
                { SvStatementExpression.wrapOperator(
                        it.function.line,
                        it.receiver,
                        SvOperatorType.NOT,
                        listOf()
                ) },
                FUNCTION_NATIVE_NOT
        )

        list.addFunction(
                "+",
                TYPE_INT,
                listOf(TYPE_INT),
                TYPE_INT,
                { RfReifiedType(TYPE_INT, RfTypeClass.INSTANCE, listOf()) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_INT_INT
        )

        list.addFunction(
                "+",
                TYPE_INT,
                listOf(TYPE_UINT),
                TYPE_UINT,
                { LangReifierUtil.implicitCast(it.receiver!!, it.args[0])
                    LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_INT_UINT
        )

        list.addFunction(
                "+",
                TYPE_UINT,
                listOf(TYPE_INT),
                TYPE_UINT,
                { LangReifierUtil.implicitCast(it.args[0], it.receiver!!)
                    LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_UINT_INT
        )

        list.addFunction(
                "+",
                TYPE_UINT,
                listOf(TYPE_UINT),
                TYPE_UINT,
                { LangReifierFunction.reifyClassNativeAddUint(it) },
                extractorNativeAdd,
                FUNCTION_NATIVE_ADD_UINT_UINT
        )
    }
}