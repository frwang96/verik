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

import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.base.ast.TypeClass.INSTANCE
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_BOOL_BOOL
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_UBIT_INT
import verikc.lang.LangSymbol.FUNCTION_ASSIGN_UBIT_UBIT
import verikc.lang.LangSymbol.FUNCTION_BLOCK_ASSIGN
import verikc.lang.LangSymbol.FUNCTION_NONBLOCK_ASSIGN
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.reify.LangReifierUtil
import verikc.ps.symbol.PsFunctionExtractorRequest
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleAssignment: LangModule {

    fun isAssign(symbol: Symbol): Boolean {
        return symbol in listOf(
                FUNCTION_ASSIGN_BOOL_BOOL,
                FUNCTION_ASSIGN_UBIT_INT,
                FUNCTION_ASSIGN_UBIT_UBIT
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
                TYPE_UBIT,
                listOf(TYPE_INT),
                listOf(INSTANCE),
                TYPE_UNIT,
                { LangReifierUtil.implicitCast(it.args[0], it.receiver!!)
                    TYPE_REIFIED_UNIT },
                { throw LineException("assignment type has not been set", it.function) },
                FUNCTION_ASSIGN_UBIT_INT
        )

        list.addFunction(
                "=",
                TYPE_UBIT,
                listOf(TYPE_UBIT),
                listOf(INSTANCE),
                TYPE_UNIT,
                { LangReifierUtil.matchTypes(it.receiver!!, it.args[0])
                    TYPE_REIFIED_UNIT },
                { throw LineException("assignment type has not been set", it.function) },
                FUNCTION_ASSIGN_UBIT_UBIT
        )

        list.addFunction(
                "=",
                null,
                listOf(),
                listOf(),
                TYPE_UNIT,
                { null },
                { request: PsFunctionExtractorRequest ->
                    SvExpressionOperator(
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
                    SvExpressionOperator(
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
