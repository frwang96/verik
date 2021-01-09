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

import verikc.base.ast.ExpressionClass.VALUE
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_ADD_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_ADD_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_MUL_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_MUL_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_SBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_SUB_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_SUB_SBIT_UBIT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.generify.LangGenerifierFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleSbitOp: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "+",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_SBIT_UBIT
        )

        list.add(
            "+",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_SBIT_SBIT
        )

        list.add(
            "-",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_SBIT_UBIT
        )

        list.add(
            "-",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_SBIT_SBIT
        )

        list.add(
            "*",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_SBIT_UBIT
        )

        list.add(
            "*",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyNativeAddSubMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_SBIT_SBIT
        )

        list.add(
            "add",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_ADD_SBIT_UBIT
        )

        list.add(
            "add",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_ADD_SBIT_SBIT
        )

        list.add(
            "sub",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_SUB_SBIT_UBIT
        )

        list.add(
            "sub",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyAddSub(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_SUB_SBIT_SBIT
        )

        list.add(
            "mul",
            TYPE_SBIT,
            listOf(TYPE_UBIT),
            listOf(VALUE),
            false,
            TYPE_UBIT,
            VALUE,
            { LangGenerifierFunction.generifyMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_MUL_SBIT_UBIT
        )

        list.add(
            "mul",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(VALUE),
            false,
            TYPE_SBIT,
            VALUE,
            { LangGenerifierFunction.generifyMul(it) },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_MUL_SBIT_SBIT
        )
    }
}