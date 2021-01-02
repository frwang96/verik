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
import verikc.lang.BitType
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.reify.LangReifierUtil
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleFunctionAssign: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "=",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(INSTANCE),
            false,
            TYPE_UNIT,
            {
                LangReifierUtil.inferWidth(it.receiver!!, it.args[0], BitType.UBIT)
                LangReifierUtil.inferWidth(it.receiver, it.args[0], BitType.SBIT)
                LangReifierUtil.matchTypes(it.receiver, it.args[0])
                TYPE_UNIT.toTypeReifiedInstance()
            },
            { throw LineException("assignment type has not been set", it.expression.line) },
            FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
        )

        list.add(
            "=",
            TYPE_UNIT,
            listOf(),
            listOf(),
            false,
            TYPE_UNIT,
            { null },
            {
                SvExpressionOperator(
                    it.expression.line,
                    it.receiver,
                    SvOperatorType.ASSIGN_BLOCKING,
                    it.args
                )
            },
            FUNCTION_NATIVE_ASSIGN_BLOCKING
        )

        list.add(
            "<=",
            TYPE_UNIT,
            listOf(),
            listOf(),
            false,
            TYPE_UNIT,
            { null },
            {
                SvExpressionOperator(
                    it.expression.line,
                    it.receiver,
                    SvOperatorType.ASSIGN_NONBLOCKING,
                    it.args
                )
            },
            FUNCTION_NATIVE_ASSIGN_NONBLOCKING
        )
    }
}
