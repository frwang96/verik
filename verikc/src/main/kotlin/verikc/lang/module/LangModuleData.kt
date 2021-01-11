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

import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.lang.LangFunctionList
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.lang.resolve.LangResolverUtil
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleData: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_data",
            TYPE_INSTANCE,
            { null },
            TYPE_DATA
        )

        list.add(
            "_logic",
            TYPE_DATA,
            { null },
            TYPE_LOGIC
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "=",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.receiver!!, it.args[0])
                LangResolverUtil.matchTypes(it.receiver, it.args[0])
                TYPE_UNIT.toTypeGenerified()
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
            VALUE,
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
            VALUE,
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

        list.add(
            "==",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.receiver!!, it.args[0])
                LangResolverUtil.matchTypes(it.receiver, it.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.EQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE
        )

        list.add(
            "!=",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverUtil.inferWidthIfBit(it.receiver!!, it.args[0])
                LangResolverUtil.matchTypes(it.receiver, it.args[0])
                TYPE_BOOL.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.NEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
        )
    }
}
