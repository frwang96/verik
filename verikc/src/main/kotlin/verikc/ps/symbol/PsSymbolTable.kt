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

package verikc.ps.symbol

import verikc.base.SymbolEntryMap
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.Lang
import verikc.ps.ast.*
import verikc.ps.extract.PsIdentifierExtractorUtil
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionProperty
import verikc.sv.ast.SvTypeExtracted

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
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in Lang.functions) {
            val functionEntry = PsFunctionEntry(
                function.symbol,
                function.extractor
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in Lang.operators) {
            val operatorEntry = PsOperatorEntry(
                operator.symbol,
                operator.extractor
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addType(type: PsModule) {
        typeEntryMap.add(PsTypeEntry(type.symbol, type.identifier) { null }, type.line)
    }

    fun addType(enum: PsEnum) {
        val typeEntry = PsTypeEntry(
            enum.symbol,
            enum.identifier
        ) { SvTypeExtracted(PsIdentifierExtractorUtil.identifierWithoutUnderscore(enum), "", "") }
        typeEntryMap.add(typeEntry, enum.line)
    }

    fun addProperty(property: PsProperty) {
        propertyEntryMap.add(PsPropertyEntry(property.symbol, property.identifier), property.line)
    }

    fun addProperty(enum: PsEnum, enumProperty: PsEnumProperty) {
        val identifier = PsIdentifierExtractorUtil.enumPropertyIdentifier(
            enum.identifier,
            enumProperty.identifier,
            enumProperty.line
        )
        propertyEntryMap.add(PsPropertyEntry(enumProperty.symbol, identifier), enumProperty.line)
    }

    fun extractType(typeReified: TypeReified, line: Line): SvTypeExtracted {
        if (typeReified.typeClass != INSTANCE) {
            throw LineException("unable to extract type $typeReified invalid type class", line)
        }
        return typeEntryMap.get(typeReified.typeSymbol, line).extractor(typeReified)
            ?: throw LineException("unable to extract type $typeReified", line)
    }

    fun extractFunction(request: PsFunctionExtractorRequest): SvExpression {
        val function = request.function
        return functionEntryMap.get(function.functionSymbol, function.line).extractor(request)
            ?: throw LineException("unable to extract function ${function.functionSymbol}", function.line)
    }

    fun extractOperator(request: PsOperatorExtractorRequest): SvExpression {
        val operator = request.operator
        return operatorEntryMap.get(operator.operatorSymbol, operator.line).extractor(request)
            ?: throw LineException("unable to extract operator ${operator.operatorSymbol}", operator.line)
    }

    fun extractProperty(expression: PsExpressionProperty): SvExpression {
        return SvExpressionProperty(
            expression.line,
            null,
            extractPropertyIdentifier(expression.propertySymbol, expression.line)
        )
    }

    fun extractTypeIdentifier(typeSymbol: Symbol, line: Line): String {
        return PsIdentifierExtractorUtil.identifierWithoutUnderscore(
            typeEntryMap.get(typeSymbol, line).identifier,
            line
        )
    }

    fun extractPropertyIdentifier(propertySymbol: Symbol, line: Line): String {
        return propertyEntryMap.get(propertySymbol, line).extractedIdentifier
    }
}
