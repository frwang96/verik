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

import verik.core.it.ItTypeClass
import verik.core.it.ItTypeReified
import verik.core.lang.LangEntryList
import verik.core.lang.LangSymbol.FUNCTION_DELAY
import verik.core.lang.LangSymbol.FUNCTION_NEGEDGE
import verik.core.lang.LangSymbol.FUNCTION_POSEDGE
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.OPERATOR_IF
import verik.core.lang.LangSymbol.OPERATOR_IF_ELSE
import verik.core.lang.LangSymbol.OPERATOR_ON
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_EVENT
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.*

object LangModuleControl: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_event",
                TYPE_INSTANCE,
                { SvTypeReified("event", "", "") },
                TYPE_EVENT
        )

        list.addFunction(
                "delay",
                null,
                listOf(TYPE_INT),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { SvStatementExpression.wrapOperator(
                        it.function.line,
                        null,
                        SvOperatorType.DELAY,
                        it.args
                ) },
                FUNCTION_DELAY
        )

        list.addFunction(
                "posedge",
                null,
                listOf(TYPE_BOOL),
                TYPE_EVENT,
                { it.typeReified = ItTypeReified(TYPE_EVENT, ItTypeClass.INSTANCE, listOf()) },
                { SvStatementExpression.wrapOperator(
                        it.function.line,
                        null,
                        SvOperatorType.POSEDGE,
                        it.args
                ) },
                FUNCTION_POSEDGE
        )

        list.addFunction(
                "negedge",
                null,
                listOf(TYPE_BOOL),
                TYPE_EVENT,
                { it.typeReified = ItTypeReified(TYPE_EVENT, ItTypeClass.INSTANCE, listOf()) },
                { SvStatementExpression.wrapOperator(
                        it.function.line,
                        null,
                        SvOperatorType.NEGEDGE,
                        it.args
                ) },
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
                { SvStatementControlBlock(
                        it.operator.line,
                        SvControlBlockType.FOREVER,
                        listOf(),
                        it.blocks
                ) },
                OPERATOR_FOREVER
        )

        list.addOperator(
                "if",
                { TYPE_UNIT },
                { TYPE_REIFIED_UNIT },
                { SvStatementControlBlock(
                        it.operator.line,
                        SvControlBlockType.IF,
                        it.args,
                        it.blocks
                ) },
                OPERATOR_IF
        )

        list.addOperator(
                "if",
                { TYPE_UNIT },
                { TYPE_REIFIED_UNIT },
                { SvStatementControlBlock(
                        it.operator.line,
                        SvControlBlockType.IF_ELSE,
                        it.args,
                        it.blocks
                ) },
                OPERATOR_IF_ELSE
        )
    }
}