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

import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.FUNCTION_DELAY
import verikc.lang.LangSymbol.FUNCTION_NEGEDGE
import verikc.lang.LangSymbol.FUNCTION_POSEDGE
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.sv.ast.*

object LangModuleControl: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
            "_event",
            TYPE_INSTANCE,
            { SvTypeExtracted("event", "", "") },
            TYPE_EVENT
        )

        list.addFunction(
            "delay",
            null,
            listOf(TYPE_INT),
            listOf(INSTANCE),
            TYPE_UNIT,
            { TYPE_REIFIED_UNIT },
            { SvExpressionOperator(it.function.line, null, SvOperatorType.DELAY, it.args) },
            FUNCTION_DELAY
        )

        list.addFunction(
            "posedge",
            null,
            listOf(TYPE_BOOL),
            listOf(INSTANCE),
            TYPE_EVENT,
            { TypeReified(TYPE_EVENT, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, null, SvOperatorType.POSEDGE, it.args) },
            FUNCTION_POSEDGE
        )

        list.addFunction(
            "negedge",
            null,
            listOf(TYPE_BOOL),
            listOf(INSTANCE),
            TYPE_EVENT,
            { TypeReified(TYPE_EVENT, INSTANCE, listOf()) },
            { SvExpressionOperator(it.function.line, null, SvOperatorType.NEGEDGE, it.args) },
            FUNCTION_NEGEDGE
        )

        list.addOperator(
            "on",
            { TYPE_UNIT },
            { TYPE_REIFIED_UNIT },
            { null },
            OPERATOR_ON
        )

        list.addOperator(
            "forever",
            { TYPE_UNIT },
            { TYPE_REIFIED_UNIT },
            { SvExpressionControlBlock(it.operator.line, SvControlBlockType.FOREVER, listOf(), it.blocks) },
            OPERATOR_FOREVER
        )

        list.addOperator(
            "if",
            { TYPE_UNIT },
            { TYPE_REIFIED_UNIT },
            { SvExpressionControlBlock(it.operator.line, SvControlBlockType.IF, it.args, it.blocks) },
            OPERATOR_IF
        )

        list.addOperator(
            "if",
            { TYPE_UNIT },
            { TYPE_REIFIED_UNIT },
            { SvExpressionControlBlock(it.operator.line, SvControlBlockType.IF_ELSE, it.args, it.blocks) },
            OPERATOR_IF_ELSE
        )
    }
}
