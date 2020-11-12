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

package verik.core.lang.modules

import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.lang.LangEntryList
import verik.core.lang.LangSymbol.FUNCTION_ASSIGN_BOOL_BOOL
import verik.core.lang.LangSymbol.FUNCTION_ASSIGN_UINT_INT
import verik.core.lang.LangSymbol.FUNCTION_ASSIGN_UINT_UINT
import verik.core.lang.LangSymbol.FUNCTION_BLOCK_ASSIGN
import verik.core.lang.LangSymbol.FUNCTION_NONBLOCK_ASSIGN
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.lang.reify.LangReifierUtil
import verik.core.ps.symbol.PsFunctionExtractorRequest
import verik.core.sv.ast.SvOperatorType
import verik.core.sv.ast.SvStatementExpression

object LangModuleAssignment: LangModule {

    fun isAssign(symbol: Symbol): Boolean {
        return symbol in listOf(
                FUNCTION_ASSIGN_BOOL_BOOL,
                FUNCTION_ASSIGN_UINT_INT,
                FUNCTION_ASSIGN_UINT_UINT
        )
    }

    override fun load(list: LangEntryList) {
        list.addFunction(
                "=",
                TYPE_BOOL,
                listOf(TYPE_BOOL),
                listOf(INSTANCE),
                TYPE_UNIT,
                { TYPE_REIFIED_UNIT },
                { throw LineException("assignment type has not been set", it.function) },
                FUNCTION_ASSIGN_BOOL_BOOL
        )

        list.addFunction(
                "=",
                TYPE_UINT,
                listOf(TYPE_INT),
                listOf(INSTANCE),
                TYPE_UNIT,
                { LangReifierUtil.implicitCast(it.args[0], it.receiver!!)
                    TYPE_REIFIED_UNIT },
                { throw LineException("assignment type has not been set", it.function) },
                FUNCTION_ASSIGN_UINT_INT
        )

        list.addFunction(
                "=",
                TYPE_UINT,
                listOf(TYPE_UINT),
                listOf(INSTANCE),
                TYPE_UNIT,
                { LangReifierUtil.matchTypes(it.receiver!!, it.args[0])
                    TYPE_REIFIED_UNIT },
                { throw LineException("assignment type has not been set", it.function) },
                FUNCTION_ASSIGN_UINT_UINT
        )

        list.addFunction(
                "=",
                null,
                listOf(),
                listOf(),
                TYPE_UNIT,
                { null },
                { request: PsFunctionExtractorRequest ->
                    SvStatementExpression.wrapOperator(
                            request.function.line,
                            request.receiver,
                            SvOperatorType.BLOCK_ASSIGN,
                            request.args
                    )
                },
                FUNCTION_BLOCK_ASSIGN
        )

        list.addFunction(
                "<=",
                null,
                listOf(),
                listOf(),
                TYPE_UNIT,
                { null },
                { request: PsFunctionExtractorRequest ->
                    SvStatementExpression.wrapOperator(
                            request.function.line,
                            request.receiver,
                            SvOperatorType.NONBLOCK_ASSIGN,
                            request.args
                    )
                },
                FUNCTION_NONBLOCK_ASSIGN
        )
    }
}