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
import verikc.lang.LangFunctionList
import verikc.lang.LangOperatorList
import verikc.lang.LangPropertyList
import verikc.lang.LangSymbol.FUNCTION_INTERNAL_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_RETURN
import verikc.lang.LangSymbol.OPERATOR_RETURN_UNIT
import verikc.lang.LangSymbol.PROPERTY_THIS
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.resolve.LangResolverOperator
import verikc.sv.ast.SvControlBlockType
import verikc.sv.ast.SvExpressionControlBlock
import verikc.sv.ast.SvExpressionOperator
import verikc.sv.ast.SvOperatorType

object LangModuleSpecial: LangModule {

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "if",
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
                    SvOperatorType.IF,
                    it.args
                )
            },
            FUNCTION_INTERNAL_IF_ELSE
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "return",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.RETURN_VOID, listOf()) },
            OPERATOR_RETURN_UNIT
        )

        list.add(
            "return",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.RETURN, it.args) },
            OPERATOR_RETURN
        )

        list.add(
            "if",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
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
            VALUE,
            { LangResolverOperator.resolveIfElse(it) },
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

    override fun loadProperties(list: LangPropertyList) {
        list.add(
            "this",
            "this",
            PROPERTY_THIS
        )
    }
}
