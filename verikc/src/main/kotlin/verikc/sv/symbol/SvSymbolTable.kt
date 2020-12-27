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

package verikc.sv.symbol

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.ps.ast.*
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvExpressionProperty
import verikc.sv.ast.SvTypeExtracted
import verikc.sv.extract.SvIdentifierExtractorUtil

class SvSymbolTable {

    private val pkgEntryMap = SymbolEntryMap<SvPkgEntry>("package")
    private val fileEntryMap = SymbolEntryMap<SvFileEntry>("file")

    private val typeEntryMap = SymbolEntryMap<SvTypeEntry>("type")
    private val functionEntryMap = SymbolEntryMap<SvFunctionEntry>("function")
    private val operatorEntryMap = SymbolEntryMap<SvOperatorEntry>("operator")
    private val propertyEntryMap = SymbolEntryMap<SvPropertyEntry>("property")

    init {
        for (type in LangDeclaration.types) {
            val typeEntry = SvTypeEntry(
                type.symbol,
                null,
                SvIdentifierExtractorUtil.identifierWithoutUnderscore(type.identifier, Line(0)),
                type.extractor
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = SvFunctionEntry(
                function.symbol,
                function.extractor
            )
            functionEntryMap.add(functionEntry, Line(0))
        }
        for (operator in LangDeclaration.operators) {
            val operatorEntry = SvOperatorEntry(
                operator.symbol,
                operator.extractor
            )
            operatorEntryMap.add(operatorEntry, Line(0))
        }
    }

    fun addPkg(pkg: PsPkg) {
        pkgEntryMap.add(SvPkgEntry(pkg.config.symbol, pkg.config.identifierSv), Line(0))
    }

    fun addFile(file: PsFile) {
        fileEntryMap.add(
            SvFileEntry(file.config.symbol, file.config.pkgSymbol),
            Line(file.config.symbol, 0)
        )
    }

    fun addType(module: PsModule) {
        val typeEntry = SvTypeEntry(
            module.symbol,
            null,
            SvIdentifierExtractorUtil.identifierWithoutUnderscore(module.identifier, module.line)
        ) { null }
        typeEntryMap.add(typeEntry, module.line)
    }

    fun addType(enum: PsEnum) {
        val pkgSymbol = fileEntryMap.get(enum.line.fileSymbol, enum.line).pkgSymbol
        val typeEntry = SvTypeEntry(
            enum.symbol,
            pkgSymbol,
            SvIdentifierExtractorUtil.identifierWithoutUnderscore(enum.identifier, enum.line)
        ) { SvTypeExtracted(SvIdentifierExtractorUtil.identifierWithoutUnderscore(enum), "", "") }
        typeEntryMap.add(typeEntry, enum.line)
    }

    fun addProperty(property: PsProperty) {
        propertyEntryMap.add(SvPropertyEntry(property.symbol, null, property.identifier), property.line)
    }

    fun addProperty(enum: PsEnum, enumProperty: PsEnumProperty) {
        val pkgSymbol = fileEntryMap.get(enum.line.fileSymbol, enum.line).pkgSymbol
        val identifier = SvIdentifierExtractorUtil.enumPropertyIdentifier(
            enum.identifier,
            enumProperty.identifier,
            enumProperty.line
        )
        propertyEntryMap.add(SvPropertyEntry(enumProperty.symbol, pkgSymbol, identifier), enumProperty.line)
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

    fun extractFunction(request: SvFunctionExtractorRequest): SvExpression {
        val function = request.function
        return functionEntryMap.get(function.functionSymbol, function.line).extractor(request)
            ?: throw LineException("unable to extract function ${function.functionSymbol}", function.line)
    }

    fun extractOperator(request: SvOperatorExtractorRequest): SvExpression {
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
