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

import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.BitType
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GEQ_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GT_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LEQ_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_LT_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_MUL_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_BOOL
import verikc.lang.LangSymbol.FUNCTION_NATIVE_SUB_INT_INT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.reify.LangReifierFunction
import verikc.lang.reify.LangReifierUtil
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionNative: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "!",
            TYPE_BOOL,
            listOf(),
            listOf(),
            false,
            TYPE_BOOL,
            { TYPE_BOOL.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.NOT, listOf()) },
            FUNCTION_NATIVE_NOT_BOOL
        )

        list.add(
            "+",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_INT_INT
        )

        list.add(
            "+",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UBIT,
            { LangReifierFunction.reifyNativeAddBit(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_UBIT_UBIT
        )

        list.add(
            "+",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeAddBit(it, BitType.SBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_SBIT_SBIT
        )

        list.add(
            "-",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SUB, it.args) },
            FUNCTION_NATIVE_SUB_INT_INT
        )

        list.add(
            "*",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_INT,
            { TYPE_INT.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.MUL, it.args) },
            FUNCTION_NATIVE_MUL_INT_INT
        )

        list.add(
            "==",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                LangReifierUtil.inferWidth(it.receiver, it.args[0], BitType.SBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.EQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE
        )

        list.add(
            "!=",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                LangReifierUtil.inferWidth(it.receiver, it.args[0], BitType.SBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.NEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
        )

        list.add(
            ">",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.GT, listOf(it.args[0])) },
            FUNCTION_NATIVE_GT_UBIT_UBIT
        )

        list.add(
            ">=",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.GEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_GEQ_UBIT_UBIT
        )

        list.add(
            "<",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.LT, listOf(it.args[0])) },
            FUNCTION_NATIVE_LT_UBIT_UBIT
        )

        list.add(
            "<=",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                TYPE_BOOL.toTypeReifiedInstance()
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.LEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_LEQ_UBIT_UBIT
        )

        list.add(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            { TYPE_BOOL.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT
        )

        list.add(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            false,
            TYPE_UBIT,
            { LangReifierFunction.reifyNativeGet(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT_INT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            false,
            TYPE_BOOL,
            { TYPE_BOOL.toTypeReifiedInstance() },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT
        )

        list.add(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            false,
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeGet(it, BitType.SBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT_INT
        )
    }
}
