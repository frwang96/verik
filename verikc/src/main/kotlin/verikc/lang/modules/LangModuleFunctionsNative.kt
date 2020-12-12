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

package verikc.lang.modules

import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.reify.LangReifierFunction
import verikc.ps.symbol.PsFunctionExtractorRequest
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionsNative: LangModule {

    override fun load(list: LangEntryList) {
        list.addFunction(
            "!",
            TYPE_BOOL,
            listOf(),
            listOf(),
            TYPE_BOOL,
            { TypeReified(TYPE_BOOL, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.NOT, listOf()) },
            FUNCTION_NATIVE_NOT
        )

        list.addFunction(
            "+",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_INT,
            { TypeReified(TYPE_INT, INSTANCE, listOf()) },
            extractorNativeAdd,
            FUNCTION_NATIVE_ADD_INT_INT
        )

        list.addFunction(
            "+",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            TYPE_UBIT,
            { LangReifierFunction.reifyClassNativeAddUbit(it) },
            extractorNativeAdd,
            FUNCTION_NATIVE_ADD_UBIT_UBIT
        )
    }

    private val extractorNativeAdd = { request: PsFunctionExtractorRequest ->
        SvExpressionOperator(
            request.function.line,
            request.receiver,
            SvOperatorType.ADD,
            request.args
        )
    }
}
