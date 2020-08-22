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

import verik.core.lang.*
import verik.core.lang.LangSymbol.FUNCTION_PUT_BOOL_BOOL
import verik.core.lang.LangSymbol.FUNCTION_PUT_UINT_INT
import verik.core.lang.LangSymbol.FUNCTION_PUT_UINT_UINT
import verik.core.lang.LangSymbol.FUNCTION_REG_BOOL_BOOL
import verik.core.lang.LangSymbol.FUNCTION_REG_UINT_INT
import verik.core.lang.LangSymbol.FUNCTION_REG_UINT_UINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.lang.reify.LangReifierUtil
import verik.core.sv.SvOperatorType
import verik.core.sv.SvStatementExpression

object LangModuleAssignment: LangModule {

    private val extractorPut = { request: LangFunctionExtractorRequest ->
        SvStatementExpression.wrapOperator(
                request.function.line,
                request.target,
                SvOperatorType.BLOCK_ASSIGN,
                request.args
        )
    }

    private val extractorReg = { request: LangFunctionExtractorRequest ->
        SvStatementExpression.wrapOperator(
                request.function.line,
                request.target,
                SvOperatorType.NBLOCK_ASSIGN,
                request.args
        )
    }

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable,
            operatorTable: LangOperatorTable
    ) {
        functionTable.add(LangFunction(
                "put",
                TYPE_BOOL,
                listOf(TYPE_BOOL),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                extractorPut,
                FUNCTION_PUT_BOOL_BOOL
        ))

        functionTable.add(LangFunction(
                "put",
                TYPE_UINT,
                listOf(TYPE_INT),
                TYPE_UNIT,
                { LangReifierUtil.implicitCast(it.args[0], it.target!!)
                    it.typeReified = TYPE_REIFIED_UNIT },
                extractorPut,
                FUNCTION_PUT_UINT_INT
        ))

        functionTable.add(LangFunction(
                "put",
                TYPE_UINT,
                listOf(TYPE_UINT),
                TYPE_UNIT,
                { LangReifierUtil.matchTypes(it.target!!, it.args[0])
                    it.typeReified = TYPE_REIFIED_UNIT },
                extractorPut,
                FUNCTION_PUT_UINT_UINT
        ))

        functionTable.add(LangFunction(
                "reg",
                TYPE_BOOL,
                listOf(TYPE_BOOL),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                extractorReg,
                FUNCTION_REG_BOOL_BOOL
        ))

        functionTable.add(LangFunction(
                "reg",
                TYPE_UINT,
                listOf(TYPE_INT),
                TYPE_UNIT,
                { LangReifierUtil.implicitCast(it.args[0], it.target!!)
                    it.typeReified = TYPE_REIFIED_UNIT },
                extractorReg,
                FUNCTION_REG_UINT_INT
        ))

        functionTable.add(LangFunction(
                "reg",
                TYPE_UINT,
                listOf(TYPE_UINT),
                TYPE_UNIT,
                { LangReifierUtil.matchTypes(it.target!!, it.args[0])
                    it.typeReified = TYPE_REIFIED_UNIT },
                extractorReg,
                FUNCTION_REG_UINT_UINT
        ))
    }
}