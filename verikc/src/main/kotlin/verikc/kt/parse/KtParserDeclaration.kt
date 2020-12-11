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
import verikc.base.SymbolIndexer
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.*

object KtParserDeclaration {

    fun parse(declaration: AlRule, indexer: SymbolIndexer): KtDeclaration {
        val child = declaration.firstAsRule()
        val annotations = child
                .childrenAs(AlRuleType.MODIFIERS)
                .flatMap { it.childrenAs(AlRuleType.ANNOTATION) }

        return when (child.type) {
            AlRuleType.CLASS_DECLARATION -> parseClassDeclaration(child, annotations, indexer)
            AlRuleType.FUNCTION_DECLARATION -> parseFunctionDeclaration(child, annotations, indexer)
            AlRuleType.PROPERTY_DECLARATION -> parsePropertyDeclaration(child, annotations, indexer)
            else -> throw LineException("class or function or property declaration expected", child.line)
        }
    }

    private fun parseClassDeclaration(
            classDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: SymbolIndexer
    ): KtPrimaryType {
        val line = classDeclaration.childAs(AlTokenType.CLASS).line
        val identifier = classDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        if (!identifier.matches(Regex("_[a-zA-Z].*"))) {
            throw LineException("type identifier should begin with a single underscore", line)
        }
        val symbol = indexer.register(identifier)

        if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

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
            KtDeclaration(it.childAs(AlRuleType.DECLARATION), indexer)
        }

        declarations.forEach {
            if (it is KtType) {
                throw LineException("nested type declaration not permitted", it.line)
            }
        }

        val parameterProperties = if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
            classDeclaration
                    .childAs(AlRuleType.PRIMARY_CONSTRUCTOR)
                    .childAs(AlRuleType.CLASS_PARAMETERS)
                    .childrenAs(AlRuleType.CLASS_PARAMETER)
                    .map { parseClassParameter(it, indexer) }
        } else listOf()

        val constructorInvocation = KtConstructorInvocation(classDeclaration, indexer)

        val isEnum = classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY)

        val constructorFunction = if (isEnum) {
            KtConstructorFunction(
                    line,
                    identifier,
                    indexer.register(identifier),
                    listOf(),
                    symbol
            )
        } else {
            KtConstructorFunction(
                    line,
                    identifier,
                    indexer.register(identifier),
                    copyParameterProperties(parameterProperties, indexer),
                    symbol
            )
        }

        val objectType = if (isEnum) {
            val objectTypeSymbol = indexer.register(identifier)
            val enumProperties = classDeclaration
                        .childAs(AlRuleType.ENUM_CLASS_BODY)
                        .childrenAs(AlRuleType.ENUM_ENTRIES)
                        .flatMap { it.childrenAs(AlRuleType.ENUM_ENTRY) }
                        .map { parseEnumEntry(it, symbol, indexer) }
            val objectProperty = KtObjectProperty(
                    line,
                    identifier,
                    indexer.register(identifier),
                    objectTypeSymbol
            )
            KtObjectType(
                    line,
                    identifier,
                    objectTypeSymbol,
                    listOf(),
                    enumProperties,
                    objectProperty
            )
        } else null

        return KtPrimaryType(
                line,
                identifier,
                symbol,
                declarations,
                annotations.map { KtAnnotationType(it) },
                parameterProperties,
                constructorInvocation,
                constructorFunction,
                objectType
        )
    }

    private fun parseFunctionDeclaration(
            functionDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: SymbolIndexer
    ): KtPrimaryFunction {
        val line = functionDeclaration.childAs(AlTokenType.FUN).line
        val identifier = functionDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        val symbol = indexer.register(identifier)

        val parameters = functionDeclaration
                .childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
                .childrenAs(AlRuleType.FUNCTION_VALUE_PARAMETER)
                .map { parseFunctionValueParameter(it, indexer) }

        val body = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
            val blockOrExpression = functionDeclaration.childAs(AlRuleType.FUNCTION_BODY).firstAsRule()
            when (blockOrExpression.type) {
                AlRuleType.BLOCK -> {
                    val typeIdentifier = if (functionDeclaration.containsType(AlRuleType.TYPE)) {
                        KtParserTypeIdentifier.parse(functionDeclaration.childAs(AlRuleType.TYPE))
                    } else "Unit"
                    KtFunctionBodyBlock(
                            typeIdentifier,
                            KtParserBlock.parseBlock(blockOrExpression, indexer)
                    )
                }
                AlRuleType.EXPRESSION -> {
                    KtFunctionBodyExpression(KtExpression(blockOrExpression, indexer))
                }
                else -> throw LineException("block or expression expected", line)
            }
        } else KtFunctionBodyBlock("Unit", KtParserBlock.emptyBlock(line, indexer))

        return KtPrimaryFunction(
                line,
                identifier,
                symbol,
                parameters,
                null,
                annotations.map { KtAnnotationFunction(it) },
                body
        )
    }

    private fun parsePropertyDeclaration(
            propertyDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: SymbolIndexer
    ): KtPrimaryProperty {
        val line = if (propertyDeclaration.containsType(AlTokenType.VAL)) {
            propertyDeclaration.childAs(AlTokenType.VAL).line
        } else {
            propertyDeclaration.childAs(AlTokenType.VAR).line
        }

        if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION), indexer)
        val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        val symbol = indexer.register(identifier)

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

    private fun parseClassParameter(classParameter: AlRule, indexer: SymbolIndexer): KtParameterProperty {
        val identifier = classParameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val symbol = indexer.register(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(classParameter.childAs(AlRuleType.TYPE))
        val expression = if (classParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(classParameter.childAs(AlRuleType.EXPRESSION), indexer)
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
            indexer: SymbolIndexer
    ): KtParameterProperty {
        val identifier = functionValueParameter
                .childAs(AlRuleType.PARAMETER)
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        val symbol = indexer.register(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parse(functionValueParameter
                .childAs(AlRuleType.PARAMETER)
                .childAs(AlRuleType.TYPE))
        val expression = if (functionValueParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(functionValueParameter.childAs(AlRuleType.EXPRESSION), indexer)
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

    private fun parseEnumEntry(enumEntry: AlRule, type: Symbol, indexer: SymbolIndexer): KtEnumProperty {
        val identifier = enumEntry.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val symbol = indexer.register(identifier)

        val args = enumEntry
                .childrenAs(AlRuleType.VALUE_ARGUMENTS)
                .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                .map { it.childAs(AlRuleType.EXPRESSION) }
                .map { KtExpression(it, indexer) }
        val arg = when (args.size) {
            0 -> null
            1 -> args[0]
            else -> throw LineException("too many arguments in enum declaration", enumEntry.line)
        }

        return KtEnumProperty(
                enumEntry.line,
                identifier,
                symbol,
                type,
                arg
        )
    }

    private fun copyParameterProperties(
            parameterProperties: List<KtParameterProperty>,
            indexer: SymbolIndexer,
    ): List<KtParameterProperty> {
        return parameterProperties.map {
            KtParameterProperty(
                    it.line,
                    it.identifier,
                    indexer.register(it.identifier),
                    it.type,
                    it.typeIdentifier,
                    it.expression
            )
        }
    }
}
