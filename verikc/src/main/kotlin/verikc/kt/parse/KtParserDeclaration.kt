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

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserDeclaration {

    fun parse(declaration: AlxTree, symbolContext: SymbolContext): KtDeclaration {
        val child = declaration.unwrap()
        val annotations = child
            .findAll(AlxRuleIndex.MODIFIERS)
            .flatMap { it.findAll(AlxRuleIndex.ANNOTATION) }

        return when (child.index) {
            AlxRuleIndex.CLASS_DECLARATION, AlxRuleIndex.OBJECT_DECLARATION ->
                parseClassOrObjectDeclaration(child, annotations, symbolContext)
            AlxRuleIndex.FUNCTION_DECLARATION -> parseFunctionDeclaration(child, annotations, symbolContext)
            AlxRuleIndex.PROPERTY_DECLARATION -> parsePropertyDeclaration(child, annotations, symbolContext)
            else -> throw LineException("class or function or property declaration expected", child.line)
        }
    }

    private fun parseClassOrObjectDeclaration(
        classOrObjectDeclaration: AlxTree,
        annotations: List<AlxTree>,
        symbolContext: SymbolContext
    ): KtType {
        val line = if (classOrObjectDeclaration.contains(AlxTerminalIndex.CLASS)) {
            classOrObjectDeclaration.find(AlxTerminalIndex.CLASS).line
        } else {
            classOrObjectDeclaration.find(AlxTerminalIndex.OBJECT).line
        }

        val identifier = classOrObjectDeclaration
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        if (!identifier.matches(Regex("_[a-zA-Z].*"))) {
            throw LineException("type identifier should begin with a single underscore", line)
        }
        val symbol = symbolContext.registerSymbol(identifier)

        if (classOrObjectDeclaration.contains(AlxRuleIndex.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val isStatic = classOrObjectDeclaration.contains(AlxTerminalIndex.OBJECT)

        val parameterProperties = if (classOrObjectDeclaration.contains(AlxRuleIndex.PRIMARY_CONSTRUCTOR)) {
            classOrObjectDeclaration
                .find(AlxRuleIndex.PRIMARY_CONSTRUCTOR)
                .find(AlxRuleIndex.CLASS_PARAMETERS)
                .findAll(AlxRuleIndex.CLASS_PARAMETER)
                .map { parseClassParameter(it, symbolContext) }
        } else listOf()

        val typeParent = KtTypeParent(classOrObjectDeclaration, symbolContext)

        val isEnum = classOrObjectDeclaration.contains(AlxRuleIndex.ENUM_CLASS_BODY)

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
                .find(AlxRuleIndex.ENUM_CLASS_BODY)
                .findAll(AlxRuleIndex.ENUM_ENTRIES)
                .flatMap { it.findAll(AlxRuleIndex.ENUM_ENTRY) }
                .map { parseEnumProperty(it, symbol, symbolContext) }
        } else listOf()

        val classMemberDeclarations = when {
            classOrObjectDeclaration.contains(AlxRuleIndex.CLASS_BODY) -> {
                classOrObjectDeclaration
                    .find(AlxRuleIndex.CLASS_BODY)
                    .find(AlxRuleIndex.CLASS_MEMBER_DECLARATIONS)
                    .findAll(AlxRuleIndex.CLASS_MEMBER_DECLARATION)
            }
            classOrObjectDeclaration.contains(AlxRuleIndex.ENUM_CLASS_BODY) -> {
                classOrObjectDeclaration
                    .find(AlxRuleIndex.ENUM_CLASS_BODY)
                    .findAll(AlxRuleIndex.CLASS_MEMBER_DECLARATIONS)
                    .flatMap { it.findAll(AlxRuleIndex.CLASS_MEMBER_DECLARATION) }
            }
            else -> listOf()
        }

        val declarations = classMemberDeclarations.map {
            if (it.contains(AlxRuleIndex.COMPANION_OBJECT))
                throw LineException("companion objects not supported", it.line)
            KtDeclaration(it.find(AlxRuleIndex.DECLARATION), symbolContext)
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
            annotations.map { KtAnnotationType(it) },
            parameterProperties,
            typeParent,
            listOf(typeConstructorFunction) + enumProperties + declarations
        )
    }

    private fun parseFunctionDeclaration(
        functionDeclaration: AlxTree,
        annotations: List<AlxTree>,
        symbolContext: SymbolContext
    ): KtFunction {
        val line = functionDeclaration.find(AlxTerminalIndex.FUN).line
        val identifier = functionDeclaration
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        val symbol = symbolContext.registerSymbol(identifier)

        val functionAnnotations = annotations.map { KtAnnotationFunction(it) }

        // TODO handle static functions
        val type = KtFunctionType.REGULAR

        val parameters = functionDeclaration
            .find(AlxRuleIndex.FUNCTION_VALUE_PARAMETERS)
            .findAll(AlxRuleIndex.FUNCTION_VALUE_PARAMETER)
            .map { parseFunctionValueParameter(it, symbolContext) }

        val returnTypeIdentifier = if (functionDeclaration.contains(AlxRuleIndex.TYPE)) {
            KtParserTypeIdentifier.parse(functionDeclaration.find(AlxRuleIndex.TYPE))
        } else "_unit"

        val block = if (functionDeclaration.contains(AlxRuleIndex.FUNCTION_BODY)) {
            KtParserBlock.parseBlock(
                functionDeclaration.find(AlxRuleIndex.FUNCTION_BODY).find(AlxRuleIndex.BLOCK),
                symbolContext
            )
        } else KtParserBlock.emptyBlock(line, symbolContext)

        return KtFunction(
            line,
            identifier,
            symbol,
            functionAnnotations,
            type,
            parameters,
            returnTypeIdentifier,
            null,
            block
        )
    }

    private fun parsePropertyDeclaration(
        propertyDeclaration: AlxTree,
        annotations: List<AlxTree>,
        symbolContext: SymbolContext
    ): KtPrimaryProperty {
        val line = if (propertyDeclaration.contains(AlxTerminalIndex.VAL)) {
            propertyDeclaration.find(AlxTerminalIndex.VAL).line
        } else {
            propertyDeclaration.find(AlxTerminalIndex.VAR).line
        }

        if (!propertyDeclaration.contains(AlxRuleIndex.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.find(AlxRuleIndex.EXPRESSION), symbolContext)
        val variableDeclaration = propertyDeclaration.find(AlxRuleIndex.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        val symbol = symbolContext.registerSymbol(identifier)

        if (variableDeclaration.contains(AlxRuleIndex.TYPE)) {
            throw LineException("explicit type declaration not supported", line)
        }

        return KtPrimaryProperty(
            line,
            identifier,
            symbol,
            null,
            annotations.map { KtAnnotationProperty(it) },
            expression
        )
    }

    private fun parseClassParameter(
        classParameter: AlxTree,
        symbolContext: SymbolContext
    ): KtParameterProperty {
        val identifier = classParameter
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(classParameter.find(AlxRuleIndex.TYPE))
        val expression = if (classParameter.contains(AlxRuleIndex.EXPRESSION)) {
            KtExpression(classParameter.find(AlxRuleIndex.EXPRESSION), symbolContext)
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
        functionValueParameter: AlxTree,
        symbolContext: SymbolContext
    ): KtParameterProperty {
        val identifier = functionValueParameter
            .find(AlxRuleIndex.PARAMETER)
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(
            functionValueParameter
                .find(AlxRuleIndex.PARAMETER)
                .find(AlxRuleIndex.TYPE)
        )
        val expression = if (functionValueParameter.contains(AlxRuleIndex.EXPRESSION)) {
            KtExpression(functionValueParameter.find(AlxRuleIndex.EXPRESSION), symbolContext)
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
        enumEntry: AlxTree,
        typeSymbol: Symbol,
        symbolContext: SymbolContext
    ): KtEnumProperty {
        val identifier = enumEntry
            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
            .find(AlxTerminalIndex.IDENTIFIER).text!!
        val symbol = symbolContext.registerSymbol(identifier)

        val args = enumEntry
            .findAll(AlxRuleIndex.VALUE_ARGUMENTS)
            .flatMap { it.findAll(AlxRuleIndex.VALUE_ARGUMENT) }
            .map { it.find(AlxRuleIndex.EXPRESSION) }
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
