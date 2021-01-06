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

        val parameterProperties = if (classOrObjectDeclaration.contains(AlRule.PRIMARY_CONSTRUCTOR)) {
            classOrObjectDeclaration
                .find(AlRule.PRIMARY_CONSTRUCTOR)
                .find(AlRule.CLASS_PARAMETERS)
                .findAll(AlRule.CLASS_PARAMETER)
                .map { parseClassParameter(it, symbolContext) }
        } else listOf()

        val isEnum = classOrObjectDeclaration.contains(AlRule.MODIFIERS) && classOrObjectDeclaration
            .find(AlRule.MODIFIERS)
            .findAll(AlRule.MODIFIER)
            .any { it.unwrap().index == AlTerminal.ENUM }

        val typeParent = KtParserTypeParent.parse(classOrObjectDeclaration, isEnum, symbolContext)

        val typeConstructorFunction = KtFunction(
            line,
            identifier,
            symbolContext.registerSymbol(identifier),
            listOf(),
            if (isEnum) listOf() else copyParameterProperties(parameterProperties, symbolContext),
            identifier,
            KtParserBlock.emptyBlock(line, symbolContext)
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
        val properties = ArrayList<KtProperty>()
        properties.addAll(enumProperties)
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
            typeConstructorFunction,
            functions,
            properties
        )
    }

    private fun parseClassParameter(classParameter: AlTree, symbolContext: SymbolContext): KtParameterProperty {
        val identifier = classParameter
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parseType(classParameter.find(AlRule.TYPE))
        val expression = if (classParameter.contains(AlRule.EXPRESSION)) {
            KtExpression(classParameter.find(AlRule.EXPRESSION), symbolContext)
        } else null

        return KtParameterProperty(
            classParameter.line,
            identifier,
            symbol,
            typeIdentifier,
            expression
        )
    }

    private fun parseEnumProperty(enumEntry: AlTree, symbolContext: SymbolContext): KtEnumProperty {
        val identifier = enumEntry
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val args = enumEntry
            .findAll(AlRule.VALUE_ARGUMENTS)
            .flatMap { it.findAll(AlRule.VALUE_ARGUMENT) }
            .map { it.find(AlRule.EXPRESSION) }
            .map { KtExpression(it, symbolContext) }
        val arg = when (args.size) {
            0 -> null
            1 -> args[0]
            else -> throw LineException("too many arguments in enum declaration", enumEntry.line)
        }

        return KtEnumProperty(enumEntry.line, identifier, symbol, arg)
    }

    private fun copyParameterProperties(
        parameterProperties: List<KtParameterProperty>,
        symbolContext: SymbolContext
    ): List<KtParameterProperty> {
        return parameterProperties.map {
            KtParameterProperty(
                it.line,
                it.identifier,
                symbolContext.registerSymbol(it.identifier),
                it.typeIdentifier,
                it.expression
            )
        }
    }
}
