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

import verikc.lang.LangFunctionList
import verikc.lang.LangOperatorList
import verikc.lang.LangSymbol.FUNCTION_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.reify.LangReifierOperator
import verikc.lang.resolve.LangResolverOperator
import verikc.sv.ast.SvControlBlockType
import verikc.sv.ast.SvExpressionControlBlock
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleOperator: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "if",
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
                    SvOperatorType.IF,
                    it.args
                )
            },
            FUNCTION_IF_ELSE
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "if",
            { TYPE_UNIT },
            { TYPE_UNIT.toTypeReifiedInstance() },
            {
                SvExpressionControlBlock(
                    it.expression.line,
                    SvControlBlockType.IF,
                    it.receiver,
                    listOf(),
                    it.blocks
                )
            },
            OPERATOR_IF
        )

        list.add(
            "if",
            { LangResolverOperator.resolveIfElse(it) },
            { LangReifierOperator.reifyIfElse(it) },
            {
                SvExpressionControlBlock(
                    it.expression.line,
                    SvControlBlockType.IF_ELSE,
                    it.receiver,
                    listOf(),
                    it.blocks
                )
            },
            OPERATOR_IF_ELSE
        )
    }
}
