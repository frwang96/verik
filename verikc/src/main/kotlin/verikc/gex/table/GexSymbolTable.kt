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

package verikc.gex.table

import verikc.base.ast.Line
import verikc.base.symbol.SymbolEntryMap
import verikc.ge.ast.GeExpressionFunction
import verikc.ge.ast.GeExpressionOperator
import verikc.lang.LangDeclaration

class GexSymbolTable {

    private val typeEntryMap = SymbolEntryMap<GexTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<GexFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<GexOperatorEntry>("operator")

    init {
        for (type in LangDeclaration.types) {
            val typeEntry = GexTypeEntry(
                type.symbol,
                type.identifier
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = GexFunctionLangEntry(
                function.symbol,
                function.argTypeClasses,
                function.isVararg
            ) { function.generifier(GexWrapper.toGe(it) as GeExpressionFunction) }
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = GexOperatorEntry(
                operator.symbol
            ) { operator.generifier(GexWrapper.toGe(it) as GeExpressionOperator) }
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }
}
