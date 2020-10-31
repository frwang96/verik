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

package verik.core.ps.symbol

import verik.core.base.SymbolEntryMap
import verik.core.base.ast.LineException
import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.lang.Lang
import verik.core.ps.ast.PsExpressionProperty
import verik.core.ps.ast.PsModule
import verik.core.ps.ast.PsProperty
import verik.core.sv.ast.SvExtractedType
import verik.core.sv.ast.SvStatement
import verik.core.sv.ast.SvStatementExpression

class PsSymbolTable {

    private val typeEntryMap = SymbolEntryMap<PsTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<PsFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<PsOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<PsPropertyEntry>("property")

    init {
        for (type in Lang.types) {
            val typeEntry = PsTypeEntry(
                    type.symbol,
                    type.identifier,
                    type.extractor
            )
            typeEntryMap.add(typeEntry, 0)
        }
        for (function in Lang.functions) {
            val functionEntry = PsFunctionEntry(
                    function.symbol,
                    function.extractor
            )
            functionEntryMap.add(functionEntry, 0)
        }
        for (operator in Lang.operators) {
            val operatorEntry = PsOperatorEntry(
                    operator.symbol,
                    operator.extractor
            )
            operatorEntryMap.add(operatorEntry, 0)
        }
    }

    fun addType(type: PsModule) {
        typeEntryMap.add(PsTypeEntry(type.symbol, type.identifier) { null }, type.line)
    }

    fun addProperty(property: PsProperty) {
        propertyEntryMap.add(PsPropertyEntry(property.symbol, property.identifier), property.line)
    }

    fun extractType(reifiedType: ReifiedType, line: Int): SvExtractedType {
        if (reifiedType.typeClass != INSTANCE) {
            throw LineException("unable to extract type $reifiedType invalid type class", line)
        }
        return typeEntryMap.get(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("unable to extract type $reifiedType", line)
    }

    fun extractFunction(request: PsFunctionExtractorRequest): SvStatement {
        val function = request.function
        return functionEntryMap.get(function.function, function.line).extractor(request)
                ?: throw LineException("unable to extract function ${function.function}", function)
    }

    fun extractOperator(request: PsOperatorExtractorRequest): SvStatement {
        val operator = request.operator
        return operatorEntryMap.get(operator.operator, operator.line).extractor(request)
                ?: throw LineException("unable to extract operator ${operator.operator}", operator)
    }

    fun extractProperty(expression: PsExpressionProperty): SvStatement {
        return SvStatementExpression.wrapProperty(
                expression.line,
                null,
                extractPropertyIdentifier(expression.property, expression.line)
        )
    }

    fun extractTypeIdentifier(type: Symbol, line: Int): String {
        return typeEntryMap.get(type, line).identifier.substring(1)
    }

    fun extractPropertyIdentifier(property: Symbol, line: Int): String {
        return propertyEntryMap.get(property, line).identifier
    }
}