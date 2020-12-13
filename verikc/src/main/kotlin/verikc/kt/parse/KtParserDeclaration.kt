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
import verikc.al.AlRuleType
import verikc.al.AlTokenType
import verikc.base.SymbolContext
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.*

object KtParserDeclaration {

    fun parse(declaration: AlRule, symbolContext: SymbolContext): KtDeclaration {
        val child = declaration.firstAsRule()
        val annotations = child
            .childrenAs(AlRuleType.MODIFIERS)
            .flatMap { it.childrenAs(AlRuleType.ANNOTATION) }

        return when (child.type) {
            AlRuleType.CLASS_DECLARATION -> parseClassDeclaration(child, annotations, symbolContext)
            AlRuleType.OBJECT_DECLARATION -> throw LineException("object declarations not supported", child.line)
            AlRuleType.FUNCTION_DECLARATION -> parseFunctionDeclaration(child, annotations, symbolContext)
            AlRuleType.PROPERTY_DECLARATION -> parsePropertyDeclaration(child, annotations, symbolContext)
            else -> throw LineException("class or function or property declaration expected", child.line)
        }
    }

    private fun parseClassDeclaration(
        classDeclaration: AlRule,
        annotations: List<AlRule>,
        symbolContext: SymbolContext
    ): KtType {
        val line = if (classDeclaration.containsType(AlTokenType.CLASS)) {
            classDeclaration.childAs(AlTokenType.CLASS).line
        } else {
            classDeclaration.childAs(AlTokenType.OBJECT).line
        }
        val identifier = classDeclaration
            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
            .firstAsTokenText()
        if (!identifier.matches(Regex("_[a-zA-Z].*"))) {
            throw LineException("type identifier should begin with a single underscore", line)
        }
        val symbol = symbolContext.registerSymbol(identifier)

        if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val parameterProperties = if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
            classDeclaration
                .childAs(AlRuleType.PRIMARY_CONSTRUCTOR)
                .childAs(AlRuleType.CLASS_PARAMETERS)
                .childrenAs(AlRuleType.CLASS_PARAMETER)
                .map { parseClassParameter(it, symbolContext) }
        } else listOf()

        val typeParent = KtTypeParent(classDeclaration, symbolContext)

        val isEnum = classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY)

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
            classDeclaration
                .childAs(AlRuleType.ENUM_CLASS_BODY)
                .childrenAs(AlRuleType.ENUM_ENTRIES)
                .flatMap { it.childrenAs(AlRuleType.ENUM_ENTRY) }
                .map { parseEnumEntry(it, symbol, symbolContext) }
        } else listOf()

        val classMemberDeclarations = when {
            classDeclaration.containsType(AlRuleType.CLASS_BODY) -> {
                classDeclaration
                    .childAs(AlRuleType.CLASS_BODY)
                    .childAs(AlRuleType.CLASS_MEMBER_DECLARATIONS)
                    .childrenAs(AlRuleType.CLASS_MEMBER_DECLARATION)
            }
            classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY) -> {
                classDeclaration
                    .childAs(AlRuleType.ENUM_CLASS_BODY)
                    .childrenAs(AlRuleType.CLASS_MEMBER_DECLARATIONS)
                    .flatMap { it.childrenAs(AlRuleType.CLASS_MEMBER_DECLARATION) }
            }
            else -> listOf()
        }

        val declarations = classMemberDeclarations.map {
            if (it.containsType(AlRuleType.COMPANION_OBJECT))
                throw LineException("companion objects not supported", it.line)
            KtDeclaration(it.childAs(AlRuleType.DECLARATION), symbolContext)
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
            annotations.map { KtAnnotationType(it) },
            parameterProperties,
            typeParent,
            listOf(typeConstructorFunction) + enumProperties + declarations
        )
    }

    private fun parseFunctionDeclaration(
        functionDeclaration: AlRule,
        annotations: List<AlRule>,
        symbolContext: SymbolContext
    ): KtFunction {
        val line = functionDeclaration.childAs(AlTokenType.FUN).line
        val identifier = functionDeclaration
            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
            .firstAsTokenText()
        val symbol = symbolContext.registerSymbol(identifier)

        val functionAnnotations = annotations.map { KtAnnotationFunction(it) }

        // TODO handle static functions
        val type = KtFunctionType.REGULAR

        val parameters = functionDeclaration
            .childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
            .childrenAs(AlRuleType.FUNCTION_VALUE_PARAMETER)
            .map { parseFunctionValueParameter(it, symbolContext) }

        val returnTypeIdentifier = if (functionDeclaration.containsType(AlRuleType.TYPE)) {
            KtParserTypeIdentifier.parse(functionDeclaration.childAs(AlRuleType.TYPE))
        } else "_unit"

        val block = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
            KtParserBlock.parseBlock(
                functionDeclaration.childAs(AlRuleType.FUNCTION_BODY).childAs(AlRuleType.BLOCK),
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
        propertyDeclaration: AlRule,
        annotations: List<AlRule>,
        symbolContext: SymbolContext
    ): KtPrimaryProperty {
        val line = if (propertyDeclaration.containsType(AlTokenType.VAL)) {
            propertyDeclaration.childAs(AlTokenType.VAL).line
        } else {
            propertyDeclaration.childAs(AlTokenType.VAR).line
        }

        if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION), symbolContext)
        val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
            .firstAsTokenText()
        val symbol = symbolContext.registerSymbol(identifier)

        if (variableDeclaration.containsType(AlRuleType.TYPE)) {
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
        classParameter: AlRule,
        symbolContext: SymbolContext
    ): KtParameterProperty {
        val identifier = classParameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(classParameter.childAs(AlRuleType.TYPE))
        val expression = if (classParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(classParameter.childAs(AlRuleType.EXPRESSION), symbolContext)
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
        functionValueParameter: AlRule,
        symbolContext: SymbolContext
    ): KtParameterProperty {
        val identifier = functionValueParameter
            .childAs(AlRuleType.PARAMETER)
            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
            .firstAsTokenText()
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(
            functionValueParameter
                .childAs(AlRuleType.PARAMETER)
                .childAs(AlRuleType.TYPE)
        )
        val expression = if (functionValueParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(functionValueParameter.childAs(AlRuleType.EXPRESSION), symbolContext)
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

    private fun parseEnumEntry(
        enumEntry: AlRule,
        typeSymbol: Symbol,
        symbolContext: SymbolContext
    ): KtEnumProperty {
        val identifier = enumEntry.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val symbol = symbolContext.registerSymbol(identifier)

        val args = enumEntry
            .childrenAs(AlRuleType.VALUE_ARGUMENTS)
            .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
            .map { it.childAs(AlRuleType.EXPRESSION) }
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
