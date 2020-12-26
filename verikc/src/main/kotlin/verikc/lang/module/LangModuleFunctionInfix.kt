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

package verikc.lang.module

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.BitType
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_MUL_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_SL_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_SR_UBIT_INT
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.reify.LangReifierFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionInfix: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "add",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangReifierFunction.reifyAddBit(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_ADD_UBIT_UBIT
        )

        list.add(
            "mul",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangReifierFunction.reifyMulBit(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_MUL_UBIT_UBIT
        )

        list.add(
            "sl",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { it.receiver!!.getTypeReifiedNotNull() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SL, it.args) },
            FUNCTION_SL_UBIT_INT
        )

        list.add(
            "sr",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { it.receiver!!.getTypeReifiedNotNull() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SR, it.args) },
            FUNCTION_SR_UBIT_INT
        )
    }
}
