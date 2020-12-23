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

package verikc.kt.parse

import verikc.al.AlRule
import verikc.al.AlTerminal
import verikc.al.AlTree
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserDeclaration {

    fun parse(declaration: AlTree, symbolContext: SymbolContext): KtDeclaration {
        val child = declaration.unwrap()
        return when (child.index) {
            AlRule.CLASS_DECLARATION, AlRule.OBJECT_DECLARATION ->
                parseClassOrObjectDeclaration(child, symbolContext)
            AlRule.FUNCTION_DECLARATION -> parseFunctionDeclaration(child, symbolContext)
            AlRule.PROPERTY_DECLARATION -> parsePropertyDeclaration(child, symbolContext)
            else -> throw LineException("class or function or property declaration expected", child.line)
        }
    }

    private fun parseClassOrObjectDeclaration(classOrObjectDeclaration: AlTree, symbolContext: SymbolContext): KtType {
        val line = if (classOrObjectDeclaration.contains(AlTerminal.CLASS)) {
            classOrObjectDeclaration.find(AlTerminal.CLASS).line
        } else {
            classOrObjectDeclaration.find(AlTerminal.OBJECT).line
        }

        val identifier = classOrObjectDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        if (!identifier.matches(Regex("_[a-zA-Z].*"))) {
            throw LineException("type identifier should begin with a single underscore", line)
        }
        val symbol = symbolContext.registerSymbol(identifier)

        if (classOrObjectDeclaration.contains(AlRule.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val isStatic = classOrObjectDeclaration.contains(AlTerminal.OBJECT)

        val annotations = if (classOrObjectDeclaration.contains(AlRule.MODIFIERS)) {
            classOrObjectDeclaration
                .find(AlRule.MODIFIERS)
                .findAll(AlRule.UNESCAPED_ANNOTATION)
                .map { KtAnnotationType(it) }
        } else listOf()

        val parameterProperties = if (classOrObjectDeclaration.contains(AlRule.PRIMARY_CONSTRUCTOR)) {
            classOrObjectDeclaration
                .find(AlRule.PRIMARY_CONSTRUCTOR)
                .find(AlRule.CLASS_PARAMETERS)
                .findAll(AlRule.CLASS_PARAMETER)
                .map { parseClassParameter(it, symbolContext) }
        } else listOf()

        val typeParent = KtTypeParent(classOrObjectDeclaration, symbolContext)

        val isEnum = classOrObjectDeclaration.contains(AlRule.ENUM_CLASS_BODY)

        val typeConstructorFunction = KtFunction(
            line,
            identifier,
            symbolContext.registerSymbol(identifier),
            listOf(),
            KtFunctionType.TYPE_CONSTRUCTOR,
            if (isEnum) listOf() else copyParameterProperties(parameterProperties, symbolContext),
            identifier,
            symbol,
            KtParserBlock.emptyBlock(line, symbolContext)
        )

        val enumProperties = if (isEnum) {
            classOrObjectDeclaration
                .find(AlRule.ENUM_CLASS_BODY)
                .findAll(AlRule.ENUM_ENTRIES)
                .flatMap { it.findAll(AlRule.ENUM_ENTRY) }
                .map { parseEnumProperty(it, symbol, symbolContext) }
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

        val declarations = classMemberDeclarations.map {
            if (it.contains(AlRule.COMPANION_OBJECT))
                throw LineException("companion objects not supported", it.line)
            KtDeclaration(it.find(AlRule.DECLARATION), symbolContext)
        }

        declarations.forEach {
            if (it is KtType) {
                throw LineException("nested type declaration not permitted", it.line)
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
            listOf(typeConstructorFunction) + enumProperties + declarations
        )
    }

    private fun parseFunctionDeclaration(functionDeclaration: AlTree, symbolContext: SymbolContext): KtFunction {
        val line = functionDeclaration.find(AlTerminal.FUN).line
        val identifier = functionDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val annotations = if (functionDeclaration.contains(AlRule.MODIFIERS)) {
            functionDeclaration
                .find(AlRule.MODIFIERS)
                .findAll(AlRule.UNESCAPED_ANNOTATION)
                .map { KtAnnotationFunction(it) }
        } else listOf()

        // TODO handle static functions
        val type = KtFunctionType.REGULAR

        val parameters = functionDeclaration
            .find(AlRule.FUNCTION_VALUE_PARAMETERS)
            .findAll(AlRule.FUNCTION_VALUE_PARAMETER)
            .map { parseFunctionValueParameter(it, symbolContext) }

        val returnTypeIdentifier = if (functionDeclaration.contains(AlRule.TYPE)) {
            KtParserTypeIdentifier.parseType(functionDeclaration.find(AlRule.TYPE))
        } else "_unit"

        val block = if (functionDeclaration.contains(AlRule.FUNCTION_BODY)) {
            KtParserBlock.parseBlock(
                functionDeclaration.find(AlRule.FUNCTION_BODY).find(AlRule.BLOCK),
                symbolContext
            )
        } else KtParserBlock.emptyBlock(line, symbolContext)

        return KtFunction(
            line,
            identifier,
            symbol,
            annotations,
            type,
            parameters,
            returnTypeIdentifier,
            null,
            block
        )
    }

    private fun parsePropertyDeclaration(propertyDeclaration: AlTree, symbolContext: SymbolContext): KtPrimaryProperty {
        val line = if (propertyDeclaration.contains(AlTerminal.VAL)) {
            propertyDeclaration.find(AlTerminal.VAL).line
        } else {
            propertyDeclaration.find(AlTerminal.VAR).line
        }

        val variableDeclaration = propertyDeclaration.find(AlRule.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        if (variableDeclaration.contains(AlRule.TYPE)) {
            throw LineException("explicit type declaration not supported", line)
        }

        val annotations = if (propertyDeclaration.contains(AlRule.MODIFIERS)) {
            propertyDeclaration
                .find(AlRule.MODIFIERS)
                .findAll(AlRule.UNESCAPED_ANNOTATION)
                .map { KtAnnotationProperty(it) }
        } else listOf()

        if (!propertyDeclaration.contains(AlRule.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.find(AlRule.EXPRESSION), symbolContext)

        return KtPrimaryProperty(
            line,
            identifier,
            symbol,
            null,
            annotations,
            expression
        )
    }

    private fun parseClassParameter(
        classParameter: AlTree,
        symbolContext: SymbolContext
    ): KtParameterProperty {
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
            null,
            typeIdentifier,
            expression
        )
    }

    private fun parseFunctionValueParameter(
        functionValueParameter: AlTree,
        symbolContext: SymbolContext
    ): KtParameterProperty {
        val identifier = functionValueParameter
            .find(AlRule.PARAMETER)
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parseType(
            functionValueParameter
                .find(AlRule.PARAMETER)
                .find(AlRule.TYPE)
        )
        val expression = if (functionValueParameter.contains(AlRule.EXPRESSION)) {
            KtExpression(functionValueParameter.find(AlRule.EXPRESSION), symbolContext)
        } else null

        return KtParameterProperty(
            functionValueParameter.line,
            identifier,
            symbol,
            null,
            typeIdentifier,
            expression
        )
    }

    private fun parseEnumProperty(
        enumEntry: AlTree,
        typeSymbol: Symbol,
        symbolContext: SymbolContext
    ): KtEnumProperty {
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

        return KtEnumProperty(
            enumEntry.line,
            identifier,
            symbol,
            typeSymbol,
            arg
        )
    }

    private fun copyParameterProperties(
        parameterProperties: List<KtParameterProperty>,
        symbolContext: SymbolContext,
    ): List<KtParameterProperty> {
        return parameterProperties.map {
            KtParameterProperty(
                it.line,
                it.identifier,
                symbolContext.registerSymbol(it.identifier),
                it.typeSymbol,
                it.typeIdentifier,
                it.expression
            )
        }
    }
}
