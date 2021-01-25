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

package verikc.sv.table

import verikc.base.ast.*
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntryMap
import verikc.lang.LangDeclaration
import verikc.lang.util.LangIdentifierUtil
import verikc.ps.ast.*
import verikc.sv.ast.*

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
                type.extractor
            )
            typeEntryMap.add(typeEntry, Line(0))
        }
        for (function in LangDeclaration.functions) {
            val functionEntry = SvFunctionExtractorEntry(
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
        for (property in LangDeclaration.properties) {
            val propertyEntry = SvPropertyEntry(
                property.symbol,
                null,
                property.extractedIdentifier
            )
            propertyEntryMap.add(propertyEntry, Line(0))
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

    fun addType(component: PsComponent) {
        val identifier = if (component.componentType == ComponentType.BUS_PORT) {
            component.busPortParentIdentifier ?: component.identifier
        } else component.identifier
        val typeEntry = SvTypeEntry(
            component.symbol,
            null
        ) { SvTypeExtracted(identifier, "", "") }
        typeEntryMap.add(typeEntry, component.line)
    }

    fun addType(enum: PsEnum) {
        val pkgSymbol = fileEntryMap.get(enum.line.fileSymbol, enum.line).pkgSymbol
        val typeEntry = SvTypeEntry(enum.symbol, pkgSymbol) {
            SvTypeExtracted(
                enum.identifier,
                "",
                ""
            )
        }
        typeEntryMap.add(typeEntry, enum.line)
    }

    fun addType(struct: PsStruct) {
        val pkgSymbol = fileEntryMap.get(struct.line.fileSymbol, struct.line).pkgSymbol
        val typeEntry = SvTypeEntry(struct.symbol, pkgSymbol) {
            SvTypeExtracted(
                struct.identifier,
                "",
                ""
            )
        }
        typeEntryMap.add(typeEntry, struct.line)
    }

    fun addType(cls: PsCls) {
        val pkgSymbol = fileEntryMap.get(cls.line.fileSymbol, cls.line).pkgSymbol
        val pkgExtractedIdentifier = pkgEntryMap.get(pkgSymbol, cls.line).extractedIdentifier
        val typeEntry = SvTypeEntry(
            cls.symbol,
            null
        ) { SvTypeExtracted("$pkgExtractedIdentifier::${cls.identifier}", "", "") }
        typeEntryMap.add(typeEntry, cls.line)
    }

    fun addFunction(methodBlock: PsMethodBlock) {
        functionEntryMap.add(SvFunctionRegularEntry(methodBlock.symbol, methodBlock.identifier), methodBlock.line)
    }

    fun addFunctionClsInstanceConstructor(instanceConstructor: PsMethodBlock) {
        functionEntryMap.add(
            SvFunctionRegularEntry(instanceConstructor.symbol, "new"),
            instanceConstructor.line
        )
    }

    fun addFunctionStructInstanceConstructor(instanceConstructor: PsMethodBlock) {
        val functionEntry = SvFunctionExtractorEntry(instanceConstructor.symbol) {
            SvExpressionOperator(
                it.expression.line,
                null,
                SvOperatorType.STRUCT_LITERAL,
                it.args
            )
        }
        functionEntryMap.add(functionEntry, instanceConstructor.line)
    }

    fun addProperty(property: PsProperty, isPkgProperty: Boolean) {
        val pkgSymbol = if (isPkgProperty) {
            fileEntryMap.get(property.line.fileSymbol, property.line).pkgSymbol
        } else null
        propertyEntryMap.add(SvPropertyEntry(property.symbol, pkgSymbol, property.identifier), property.line)
    }

    fun addPropertyEnum(property: PsProperty, enumIdentifier: String) {
        val pkgSymbol = fileEntryMap.get(property.line.fileSymbol, property.line).pkgSymbol
        val identifier = LangIdentifierUtil.enumPropertyIdentifier(enumIdentifier, property.identifier)
        propertyEntryMap.add(
            SvPropertyEntry(property.symbol, pkgSymbol, identifier),
            property.line
        )
    }

    fun addPropertyModuleTopObject(property: PsProperty, module: PsComponent) {
        val identifier = "\$root.${module.identifier}"
        propertyEntryMap.add(SvPropertyEntry(property.symbol, null, identifier), property.line)
    }

    fun extractType(typeGenerified: TypeGenerified, line: Line): SvTypeExtracted {
        val typeEntry = typeEntryMap.get(typeGenerified.typeSymbol, line)
        val typesExtracted = typeGenerified.args.map {
            if (it is TypeArgumentTypeGenerified) extractType(it.typeGenerified, line)
            else null
        }
        val typeExtracted = typeEntry.extractor(SvTypeExtractorRequest(typeGenerified, typesExtracted))
            ?: throw LineException("unable to extract type $typeGenerified", line)
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
        val expression = request.expression
        return when (val langEntry = functionEntryMap.get(expression.functionSymbol, expression.line)) {
            is SvFunctionExtractorEntry -> {
                langEntry.extractor(request)
                    ?: throw LineException("unable to extract function ${expression.functionSymbol}", expression.line)
            }
            is SvFunctionRegularEntry -> {
                return SvExpressionFunction(expression.line, request.receiver, langEntry.identifier, request.args)
            }
        }
    }

    fun extractOperator(request: SvOperatorExtractorRequest): SvExpression {
        val expression = request.expression
        return operatorEntryMap.get(expression.operatorSymbol, expression.line).extractor(request)
            ?: throw LineException("unable to extract operator ${expression.operatorSymbol}", expression.line)
    }

    fun extractProperty(request: SvPropertyExtractorRequest): SvExpression {
        val expression = request.expression
        val propertyEntry = propertyEntryMap.get(expression.propertySymbol, expression.line)

        // enum property receivers are dropped
        val receiver = if (propertyEntry.pkgSymbol == null) request.receiver else null

        return SvExpressionProperty(
            expression.line,
            receiver,
            extractPropertyIdentifier(expression.propertySymbol, expression.line)
        )
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
