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
import verikc.lang.BitType
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ADD_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_EQUALITY_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_SBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_GET_UBIT_INT_INT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NOT_BOOL
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

    override fun load(list: LangEntryList) {
        list.addFunction(
            "!",
            TYPE_BOOL,
            listOf(),
            listOf(),
            TYPE_BOOL,
            { TypeReified(TYPE_BOOL, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.NOT, listOf()) },
            FUNCTION_NATIVE_NOT_BOOL
        )

        list.addFunction(
            "+",
            TYPE_INT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_INT,
            { TypeReified(TYPE_INT, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_INT_INT
        )

        list.addFunction(
            "+",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            TYPE_UBIT,
            { LangReifierFunction.reifyNativeAddBit(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_UBIT_UBIT
        )

        list.addFunction(
            "+",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeAddBit(it, BitType.SBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.ADD, it.args) },
            FUNCTION_NATIVE_ADD_SBIT_SBIT
        )

        list.addFunction(
            "==",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            TYPE_BOOL,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                LangReifierUtil.inferWidth(it.receiver, it.args[0], BitType.SBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TypeReified(TYPE_BOOL, INSTANCE, listOf())
            },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.EQUALITY, listOf(it.args[0])) },
            FUNCTION_NATIVE_EQUALITY_INSTANCE_INSTANCE
        )

        list.addFunction(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_BOOL,
            { TypeReified(TYPE_BOOL, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT
        )

        list.addFunction(
            "get",
            TYPE_UBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            TYPE_UBIT,
            { LangReifierFunction.reifyNativeGet(it, BitType.UBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_UBIT_INT_INT
        )

        list.addFunction(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_BOOL,
            { TypeReified(TYPE_BOOL, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_BIT, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT
        )

        list.addFunction(
            "get",
            TYPE_SBIT,
            listOf(TYPE_INT, TYPE_INT),
            listOf(INSTANCE, INSTANCE),
            TYPE_SBIT,
            { LangReifierFunction.reifyNativeGet(it, BitType.SBIT) },
            { SvExpressionOperator(it.function.line, it.receiver, SvOperatorType.SELECT_PART, it.args) },
            FUNCTION_NATIVE_GET_SBIT_INT_INT
        )
    }
}
