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
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserType {

    fun parse(classOrObjectDeclaration: AlTree, symbolContext: SymbolContext): KtType {
        val line = if (classOrObjectDeclaration.contains(AlTerminal.CLASS)) {
            classOrObjectDeclaration.find(AlTerminal.CLASS).line
        } else {
            classOrObjectDeclaration.find(AlTerminal.OBJECT).line
        }

        val identifier = classOrObjectDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        KtIdentifierParserUtil.checkClassOrObjectIdentifier(identifier, line)
        val symbol = symbolContext.registerSymbol(identifier)

        if (classOrObjectDeclaration.contains(AlRule.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val isStatic = classOrObjectDeclaration.contains(AlTerminal.OBJECT)

        val annotations = if (classOrObjectDeclaration.contains(AlRule.MODIFIERS)) {
            KtParserAnnotation.parseAnnotationsType(classOrObjectDeclaration.find(AlRule.MODIFIERS))
        } else listOf()

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

        val typeConstructorFunction = KtFunction(
            line,
            identifier,
            symbolContext.registerSymbol(identifier),
            listOf(),
            if (isEnum) listOf() else classParameters.map { parseClassParameter(it, symbolContext) },
            identifier,
            KtParserBlock.emptyBlock(line, symbolContext)
        )

        val instanceConstructorFunction = if (!isStatic
            && typeParent.typeIdentifier !in listOf("_bus", "_bport", "_cport", "_enum", "_struct", "_module")
        ) {
            val functionIdentifier = KtIdentifierParserUtil.identifierWithoutUnderscore(identifier, line)
            KtFunction(
                line,
                functionIdentifier,
                symbolContext.registerSymbol(functionIdentifier),
                listOf(),
                listOf(),
                identifier,
                KtParserBlock.emptyBlock(line, symbolContext)
            )
        } else null

        val enumConstructorFunction = if (isEnum) {
            KtFunction(
                line,
                identifier,
                symbolContext.registerSymbol(identifier),
                listOf(),
                classParameters.map { parseClassParameter(it, symbolContext) },
                identifier,
                KtParserBlock.emptyBlock(line, symbolContext)
            ).also {
                if ( it.parameterProperties.size != 1
                    || it.parameterProperties[0].identifier != "value"
                    || it.parameterProperties[0].typeIdentifier != "_ubit"
                ) throw LineException("enum constructor function does not have the appropriate parameters", line)
            }
        } else null

        val enumProperties = if (isEnum && classOrObjectDeclaration.contains(AlRule.ENUM_CLASS_BODY)) {
            classOrObjectDeclaration
                .find(AlRule.ENUM_CLASS_BODY)
                .findAll(AlRule.ENUM_ENTRIES)
                .flatMap { it.findAll(AlRule.ENUM_ENTRY) }
                .map { parseEnumEntry(it, identifier, symbolContext) }
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
        val properties = ArrayList<KtProperty>()
        classMemberDeclarations.forEach {
            if (it.contains(AlRule.COMPANION_OBJECT))
                throw LineException("companion objects not supported", it.line)

            when (val declaration = KtParserDeclaration.parse(it.find(AlRule.DECLARATION), symbolContext)) {
                is KtType -> throw LineException("nested type declaration not permitted", declaration.line)
                is KtFunction -> functions.add(declaration)
                is KtProperty -> properties.add(declaration)
            }
        }

        return KtType(
            line,
            identifier,
            symbol,
            isStatic,
            annotations,
            parameterProperties,
            typeParent,
            typeObject,
            typeConstructorFunction,
            instanceConstructorFunction,
            enumConstructorFunction,
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

    private fun parseEnumEntry(enumEntry: AlTree, typeIdentifier: String, symbolContext: SymbolContext): KtProperty {
        val identifier = enumEntry
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val expressions = enumEntry
            .findAll(AlRule.VALUE_ARGUMENTS)
            .flatMap { it.findAll(AlRule.VALUE_ARGUMENT) }
            .map { it.find(AlRule.EXPRESSION) }
            .map { KtExpression(it, symbolContext) }
        val expression = when (expressions.size) {
            0 -> KtExpressionFunction(enumEntry.line, typeIdentifier, null, listOf())
            1 -> KtExpressionFunction(enumEntry.line, typeIdentifier, null, listOf(expressions[0]))
            else -> throw LineException("too many arguments in enum entry", enumEntry.line)
        }

        return KtProperty(
            enumEntry.line,
            identifier,
            symbol,
            MutabilityType.VAL,
            listOf(),
            null,
            expression
        )
    }
}
