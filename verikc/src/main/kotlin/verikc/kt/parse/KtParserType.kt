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

package verikc.kt.parse

import verikc.al.ast.AlRule
import verikc.al.ast.AlTerminal
import verikc.al.ast.AlTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.util.LangIdentifierUtil

object KtParserType {

    fun parse(classOrObjectDeclaration: AlTree, topIdentifier: String?, symbolContext: SymbolContext): KtType {
        val line = if (classOrObjectDeclaration.contains(AlTerminal.CLASS)) {
            classOrObjectDeclaration.find(AlTerminal.CLASS).line
        } else {
            classOrObjectDeclaration.find(AlTerminal.OBJECT).line
        }

        val identifier = classOrObjectDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        if (classOrObjectDeclaration.contains(AlRule.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val isStatic = classOrObjectDeclaration.contains(AlTerminal.OBJECT)

        val classParameters = if (classOrObjectDeclaration.contains(AlRule.PRIMARY_CONSTRUCTOR)) {
            classOrObjectDeclaration
                .find(AlRule.PRIMARY_CONSTRUCTOR)
                .find(AlRule.CLASS_PARAMETERS)
                .findAll(AlRule.CLASS_PARAMETER)
        } else listOf()

        val parameterProperties = classParameters.map { parseClassParameter(it, symbolContext) }

        val isEnum = classOrObjectDeclaration.contains(AlRule.MODIFIERS) && classOrObjectDeclaration
            .find(AlRule.MODIFIERS)
            .findAll(AlRule.MODIFIER)
            .any { it.unwrap().index == AlTerminal.ENUM }

        val typeParent = KtParserTypeParent.parse(classOrObjectDeclaration, isEnum, symbolContext)

        val typeObject = KtProperty(
            line,
            identifier,
            symbolContext.registerSymbol(identifier),
            MutabilityType.VAL,
            listOf(),
            identifier,
            null
        )

        val topObject = if (identifier == topIdentifier) {
            KtProperty(
                line,
                "top",
                symbolContext.registerSymbol("top"),
                MutabilityType.VAL,
                listOf(),
                identifier,
                null
            )
        } else null

        val typeConstructorIdentifier = LangIdentifierUtil.typeConstructorIdentifier(identifier)
        val typeConstructor = KtFunction(
            line,
            typeConstructorIdentifier,
            symbolContext.registerSymbol(typeConstructorIdentifier),
            listOf(),
            classParameters.map { parseClassParameter(it, symbolContext) },
            identifier,
            null
        )

        val enumProperties = if (isEnum && classOrObjectDeclaration.contains(AlRule.ENUM_CLASS_BODY)) {
            classOrObjectDeclaration
                .find(AlRule.ENUM_CLASS_BODY)
                .findAll(AlRule.ENUM_ENTRIES)
                .flatMap { it.findAll(AlRule.ENUM_ENTRY) }
                .map { parseEnumProperty(it, symbolContext) }
        } else listOf()

        val classMemberDeclarations = when {
            classOrObjectDeclaration.contains(AlRule.CLASS_BODY) -> {
                classOrObjectDeclaration
                    .find(AlRule.CLASS_BODY)
                    .find(AlRule.CLASS_MEMBER_DECLARATIONS)
                    .findAll(AlRule.CLASS_MEMBER_DECLARATION)
            }
            classOrObjectDeclaration.contains(AlRule.ENUM_CLASS_BODY) -> {
                classOrObjectDeclaration
                    .find(AlRule.ENUM_CLASS_BODY)
                    .findAll(AlRule.CLASS_MEMBER_DECLARATIONS)
                    .flatMap { it.findAll(AlRule.CLASS_MEMBER_DECLARATION) }
            }
            else -> listOf()
        }

        val functions = ArrayList<KtFunction>()
        val initFunctions = ArrayList<KtFunction>()
        val properties = ArrayList<KtProperty>()
        classMemberDeclarations.forEach {
            if (it.contains(AlRule.COMPANION_OBJECT))
                throw LineException("companion objects not supported", it.line)

            when (val declaration = KtParserDeclaration.parse(it.find(AlRule.DECLARATION), null, symbolContext)) {
                is KtType -> throw LineException("nested type declaration not permitted", declaration.line)
                is KtFunction -> {
                    if (declaration.identifier == "init") {
                        initFunctions.add(declaration)
                    } else {
                        functions.add(declaration)
                    }
                }
                is KtProperty -> properties.add(declaration)
            }
        }

        val instanceConstructor = getInstanceConstructor(
            line,
            identifier,
            isStatic,
            typeParent,
            initFunctions,
            properties,
            symbolContext
        )

        return KtType(
            line,
            identifier,
            symbol,
            isStatic,
            parameterProperties,
            typeParent,
            typeObject,
            topObject,
            typeConstructor,
            instanceConstructor,
            enumProperties,
            functions,
            properties
        )
    }

    private fun parseClassParameter(classParameter: AlTree, symbolContext: SymbolContext): KtProperty {
        val identifier = classParameter
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        if (classParameter.contains(AlTerminal.VAR))
            throw LineException("class parameter cannot be mutable", classParameter.line)

        val typeIdentifier = KtParserTypeIdentifier.parseType(classParameter.find(AlRule.TYPE))
        val expression = if (classParameter.contains(AlRule.EXPRESSION)) {
            KtExpression(classParameter.find(AlRule.EXPRESSION), symbolContext)
        } else null

        return KtProperty(
            classParameter.line,
            identifier,
            symbol,
            MutabilityType.VAL,
            listOf(),
            typeIdentifier,
            expression
        )
    }

    private fun parseEnumProperty(enumEntry: AlTree, symbolContext: SymbolContext): KtProperty {
        val identifier = enumEntry
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        return KtProperty(enumEntry.line, identifier, symbol, MutabilityType.VAL, listOf(), null, null)
    }

    private fun getInstanceConstructor(
        line: Line,
        identifier: String,
        isStatic: Boolean,
        typeParent: KtTypeParent,
        initFunctions: List<KtFunction>,
        properties: List<KtProperty>,
        symbolContext: SymbolContext
    ): KtFunction? {
        val instanceConstructorIdentifier = LangIdentifierUtil.instanceConstructorIdentifier(identifier)

        if (!typeParent.matches("Bus", "BusPort", "ClockPort", "Enum", "Struct", "Module")) {
            return if (isStatic) {
                if (initFunctions.isNotEmpty())
                    throw LineException("object declaration cannot contain init function", initFunctions[0].line)
                null
            } else {
                when (initFunctions.size) {
                    0 -> {
                        KtFunction(
                            line,
                            instanceConstructorIdentifier,
                            symbolContext.registerSymbol(instanceConstructorIdentifier),
                            listOf(),
                            listOf(),
                            identifier,
                            null,
                        )
                    }
                    1 -> {
                        val initFunction = initFunctions[0]
                        if (initFunction.returnTypeIdentifier != "Unit")
                            throw LineException("init function must not have a return type", initFunction.line)
                        KtFunction(
                            initFunction.line,
                            instanceConstructorIdentifier,
                            initFunction.symbol,
                            initFunction.annotations,
                            initFunction.parameterProperties,
                            identifier,
                            initFunction.block
                        )
                    }
                    else -> throw LineException("overloading of init functions not supported", initFunctions[0].line)
                }
            }
        } else {
            if (initFunctions.isNotEmpty())
                throw LineException("init functions can only be declared in classes", initFunctions[0].line)
        }

        if (typeParent.matches("Struct")) {
            val instanceConstructorParameterProperties = properties.map {
                KtProperty(
                    it.line,
                    it.identifier,
                    symbolContext.registerSymbol(it.identifier),
                    MutabilityType.VAL,
                    listOf(),
                    it.typeIdentifier,
                    it.expression
                )
            }
            return KtFunction(
                line,
                instanceConstructorIdentifier,
                symbolContext.registerSymbol(instanceConstructorIdentifier),
                listOf(),
                instanceConstructorParameterProperties,
                identifier,
                null
            )
        }

        return null
    }
}
