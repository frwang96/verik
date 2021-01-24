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
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_BLOCKING
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_EQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.TYPE_BOOLEAN
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_ENUM
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_LOGIC
import verikc.lang.LangSymbol.TYPE_STRUCT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.lang.resolve.LangResolverCommon
import verikc.lang.resolve.LangResolverFunction
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleData: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "Data",
            TYPE_INSTANCE,
            false,
            { null },
            TYPE_DATA
        )

        list.add(
            "Logic",
            TYPE_DATA,
            false,
            { null },
            TYPE_LOGIC
        )

        list.add(
            "Enum",
            TYPE_DATA,
            false,
            { null },
            TYPE_ENUM
        )

        list.add(
            "Struct",
            TYPE_DATA,
            false,
            { null },
            TYPE_STRUCT
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
            { LangResolverFunction.resolveAssign(it) },
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
            FUNCTION_INTERNAL_ASSIGN_BLOCKING
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
            FUNCTION_INTERNAL_ASSIGN_NONBLOCKING
        )

        list.add(
            "==",
            TYPE_INSTANCE,
            listOf(TYPE_INSTANCE),
            listOf(VALUE),
            false,
            VALUE,
            {
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                LangResolverCommon.matchTypes(it.expression.receiver, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
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
                LangResolverCommon.inferWidthIfBit(it.expression.receiver!!, it.expression.args[0])
                LangResolverCommon.matchTypes(it.expression.receiver, it.expression.args[0])
                TYPE_BOOLEAN.toTypeGenerified()
            },
            { SvExpressionOperator(it.expression.line, it.receiver, SvOperatorType.NEQ, listOf(it.args[0])) },
            FUNCTION_NATIVE_NEQ_INSTANCE_INSTANCE
        )
    }
}
