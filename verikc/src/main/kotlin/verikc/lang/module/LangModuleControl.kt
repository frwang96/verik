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
import verikc.lang.LangSymbol.FUNCTION_DELAY_INT
import verikc.lang.LangSymbol.FUNCTION_NEGEDGE_BOOL
import verikc.lang.LangSymbol.FUNCTION_POSEDGE_BOOL
import verikc.lang.LangSymbol.FUNCTION_WAIT_EVENT
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.lang.LangSymbol.OPERATOR_REPEAT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.lang.LangTypeList
import verikc.sv.ast.*

object LangModuleControl: LangModule {

    override fun loadTypes(list: LangTypeList) {
        list.add(
            "_event",
            TYPE_INSTANCE,
            { SvTypeExtracted("event", "", "") },
            TYPE_EVENT
        )
    }

    override fun loadFunctions(list: LangFunctionList) {
        list.add(
            "delay",
            null,
            listOf(TYPE_INT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.DELAY, it.args) },
            FUNCTION_DELAY_INT
        )

        list.add(
            "wait",
            null,
            listOf(TYPE_EVENT),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.AT, it.args) },
            FUNCTION_WAIT_EVENT
        )

        list.add(
            "posedge",
            null,
            listOf(TYPE_BOOL),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_EVENT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.POSEDGE, it.args) },
            FUNCTION_POSEDGE_BOOL
        )

        list.add(
            "negedge",
            null,
            listOf(TYPE_BOOL),
            listOf(VALUE),
            false,
            VALUE,
            { TYPE_EVENT.toTypeGenerified() },
            { SvExpressionOperator(it.expression.line, null, SvOperatorType.NEGEDGE, it.args) },
            FUNCTION_NEGEDGE_BOOL
        )
    }

    override fun loadOperators(list: LangOperatorList) {
        list.add(
            "on",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            { null },
            OPERATOR_ON
        )

        list.add(
            "forever",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            {
                SvExpressionControlBlock(
                    it.expression.line,
                    SvControlBlockType.FOREVER,
                    null,
                    listOf(),
                    it.blocks
                )
            },
            OPERATOR_FOREVER
        )

        list.add(
            "repeat",
            VALUE,
            { TYPE_UNIT.toTypeGenerified() },
            {
                SvExpressionControlBlock(
                    it.expression.line,
                    SvControlBlockType.REPEAT,
                    null,
                    it.args,
                    it.blocks
                )
            },
            OPERATOR_REPEAT
        )
    }
}
