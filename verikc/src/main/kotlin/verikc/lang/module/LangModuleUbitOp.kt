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

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_ADD_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_MUL_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_MUL_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_SUB_UBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_SUB_UBIT_UBIT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.generify.LangGenerifierFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleUbitOp: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "+",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_UBIT_UBIT
        )

        list.add(
            "+",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_UBIT_SBIT
        )

        list.add(
            "-",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_UBIT_UBIT
        )

        list.add(
            "-",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_UBIT_SBIT
        )

        list.add(
            "*",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_UBIT_UBIT
        )

        list.add(
            "*",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_UBIT_SBIT
        )

        list.add(
            "add",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_ADD_UBIT_UBIT
        )

        list.add(
            "add",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_ADD_UBIT_SBIT
        )

        list.add(
            "sub",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_SUB_UBIT_UBIT
        )

        list.add(
            "sub",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_SUB_UBIT_SBIT
        )

        list.add(
            "mul",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_MUL_UBIT_UBIT
        )

        list.add(
            "mul",
            TYPE_UBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangGenerifierFunction.generifyMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_MUL_UBIT_SBIT
        )
    }
}