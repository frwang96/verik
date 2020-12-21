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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.Lang
import verikc.ps.ast.*
import verikc.ps.extract.PsIdentifierExtractorUtil
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionProperty
import verikc.sv.ast.SvTypeExtracted

class PsSymbolTable {

    private val pkgEntryMap = SymbolEntryMap<PsPkgEntry>("package")
    private val fileEntryMap = SymbolEntryMap<PsFileEntry>("file")

    private val typeEntryMap = SymbolEntryMap<PsTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<PsFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<PsOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<PsPropertyEntry>("property")

    private val componentEntryMap = SymbolEntryMap<PsComponentEntry>("component")

    init {
        for (type in Lang.types) {
            val typeEntry = PsTypeEntry(
                type.symbol,
                null,
                PsIdentifierExtractorUtil.identifierWithoutUnderscore(type.identifier, Line(0)),
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

    fun addPkg(pkg: PsPkg) {
        pkgEntryMap.add(PsPkgEntry(pkg.config.symbol, pkg.config.identifierSv), Line(0))
    }

    fun addFile(file: PsFile) {
        fileEntryMap.add(
            PsFileEntry(file.config.symbol, file.config.pkgSymbol),
            Line(file.config.symbol, 0)
        )
    }

    fun addType(module: PsModule) {
        val typeEntry = PsTypeEntry(
            module.symbol,
            null,
            PsIdentifierExtractorUtil.identifierWithoutUnderscore(module.identifier, module.line)
        ) { null }
        typeEntryMap.add(typeEntry, module.line)
        componentEntryMap.add(PsComponentEntry(module.symbol, module.ports), module.line)
    }

    fun addType(enum: PsEnum) {
        val pkgSymbol = fileEntryMap.get(enum.line.fileSymbol, enum.line).pkgSymbol
        val typeEntry = PsTypeEntry(
            enum.symbol,
            pkgSymbol,
            PsIdentifierExtractorUtil.identifierWithoutUnderscore(enum.identifier, enum.line)
        ) { SvTypeExtracted(PsIdentifierExtractorUtil.identifierWithoutUnderscore(enum), "", "") }
        typeEntryMap.add(typeEntry, enum.line)
    }

    fun addProperty(property: PsProperty) {
        propertyEntryMap.add(PsPropertyEntry(property.symbol, null, property.identifier), property.line)
    }

    fun addProperty(enum: PsEnum, enumProperty: PsEnumProperty) {
        val pkgSymbol = fileEntryMap.get(enum.line.fileSymbol, enum.line).pkgSymbol
        val identifier = PsIdentifierExtractorUtil.enumPropertyIdentifier(
            enum.identifier,
            enumProperty.identifier,
            enumProperty.line
        )
        propertyEntryMap.add(PsPropertyEntry(enumProperty.symbol, pkgSymbol, identifier), enumProperty.line)
    }

    fun getComponentPorts(typeSymbol: Symbol, line: Line): List<PsPort> {
        return componentEntryMap.get(typeSymbol, line).ports
    }

    fun extractType(typeReified: TypeReified, line: Line): SvTypeExtracted {
        if (typeReified.typeClass != INSTANCE) {
            throw LineException("unable to extract type $typeReified invalid type class", line)
        }
        val typeEntry = typeEntryMap.get(typeReified.typeSymbol, line)
        val typeExtracted = typeEntry.extractor(typeReified)
            ?: throw LineException("unable to extract type $typeReified", line)
        return if (typeEntry.pkgSymbol != null) {
            val pkgExtractedIdentifier = pkgEntryMap.get(typeEntry.pkgSymbol, line).extractedIdentifier
            SvTypeExtracted(
                pkgExtractedIdentifier + "::" + typeExtracted.identifier,
                typeExtracted.packed,
                typeExtracted.unpacked
            )
        } else {
            typeExtracted
        }
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
        return typeEntryMap.get(typeSymbol, line).extractedIdentifier
    }

    fun extractPropertyIdentifier(propertySymbol: Symbol, line: Line): String {
        val propertyEntry = propertyEntryMap.get(propertySymbol, line)
        return if (propertyEntry.pkgSymbol != null) {
            val pkgExtractedIdentifier = pkgEntryMap.get(propertyEntry.pkgSymbol, line).extractedIdentifier
            pkgExtractedIdentifier + "::" + propertyEntry.extractedIdentifier
        } else {
            propertyEntry.extractedIdentifier
        }
    }
}
