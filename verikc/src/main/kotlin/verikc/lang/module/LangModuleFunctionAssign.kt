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

import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.symbol.Symbol
import verikc.lang.BitType
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_SBIT_SBIT
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_UBIT_UBIT
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.reify.LangReifierUtil
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionAssign: LangModule {

    fun isAssign(symbol: Symbol): Boolean {
        return symbol in listOf(
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE,
            FUNCTION_NATIVE_ASSIGN_UBIT_UBIT,
            FUNCTION_NATIVE_ASSIGN_SBIT_SBIT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "=",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            false,
            TYPE_UNIT,
            {
                LangReifierUtil.matchTypes(it.receiver!!, it.args[0])
                TYPE_UNIT.toTypeReifiedInstance()
            },
            { throw LineException("assignment type has not been set", it.function.line) },
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        )

        list.add(
            "=",
            TYPE_UBIT,
            listOf(TYPE_UBIT),
            listOf(INSTANCE),
            false,
            TYPE_UNIT,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TYPE_UNIT.toTypeReifiedInstance()
            },
            { throw LineException("assignment type has not been set", it.function.line) },
            FUNCTION_NATIVE_ASSIGN_UBIT_UBIT
        )

        list.add(
            "=",
            TYPE_SBIT,
            listOf(TYPE_SBIT),
            listOf(INSTANCE),
            false,
            TYPE_UNIT,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.SBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TYPE_UNIT.toTypeReifiedInstance()
            },
            { throw LineException("assignment type has not been set", it.function.line) },
            FUNCTION_NATIVE_ASSIGN_SBIT_SBIT
        )

        list.add(
            "=",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_UNIT,
            { null },
            {
                SvExpressionOperator(
                    it.function.line,
                    it.receiver,
                    SvOperatorType.ASSIGN_BLOCKING,
                    it.args
                )
            },
            FUNCTION_NATIVE_ASSIGN_BLOCKING
        )

        list.add(
            "<=",
            null,
            listOf(),
            listOf(),
            false,
            TYPE_UNIT,
            { null },
            {
                SvExpressionOperator(
                    it.function.line,
                    it.receiver,
                    SvOperatorType.ASSIGN_NONBLOCKING,
                    it.args
                )
            },
            FUNCTION_NATIVE_ASSIGN_NONBLOCKING
        )
    }
}
