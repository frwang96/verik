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
import verik.core.lang.*
import verik.core.lang.LangSymbol.FUNCTION_DELAY
import verik.core.lang.LangSymbol.FUNCTION_NEGEDGE
import verik.core.lang.LangSymbol.FUNCTION_POSEDGE
import verik.core.lang.LangSymbol.OPERATOR_ON
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_EVENT
import verik.core.lang.LangSymbol.TYPE_INSTANCE
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_REIFIED_UNIT
import verik.core.lang.LangSymbol.TYPE_UNIT
import verik.core.sv.SvExpressionOperator
import verik.core.sv.SvOperatorIdentifier
import verik.core.sv.SvTypeReified

object LangModuleControl: LangModule {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable,
            operatorTable: LangOperatorTable
    ) {
        typeTable.add(LangType(
                "_event",
                TYPE_INSTANCE,
                { SvTypeReified("event", "", "") },
                TYPE_EVENT
        ))

        functionTable.add(LangFunction(
                "delay",
                null,
                listOf(TYPE_INT),
                TYPE_UNIT,
                { it.typeReified = TYPE_REIFIED_UNIT },
                { SvExpressionOperator(
                        it.function.line,
                        null,
                        SvOperatorIdentifier.DELAY,
                        it.args
                ) },
                FUNCTION_DELAY
        ))

        functionTable.add(LangFunction(
                "posedge",
                null,
                listOf(TYPE_BOOL),
                TYPE_EVENT,
                { it.typeReified = ItTypeReified(TYPE_EVENT, ItTypeClass.INSTANCE, listOf()) },
                { SvExpressionOperator(
                        it.function.line,
                        null,
                        SvOperatorIdentifier.POSEDGE,
                        it.args
                ) },
                FUNCTION_POSEDGE
        ))

        functionTable.add(LangFunction(
                "negedge",
                null,
                listOf(TYPE_BOOL),
                TYPE_EVENT,
                { it.typeReified = ItTypeReified(TYPE_EVENT, ItTypeClass.INSTANCE, listOf()) },
                { SvExpressionOperator(
                        it.function.line,
                        null,
                        SvOperatorIdentifier.NEGEDGE,
                        it.args
                ) },
                FUNCTION_NEGEDGE
        ))

        operatorTable.add(LangOperator(
                { TYPE_UNIT },
                OPERATOR_ON
        ))
    }
}