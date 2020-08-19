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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.al.AlTokenType
import verik.core.base.LineException
import verik.core.base.Symbol

object KtDeclarationParser {

    fun parse(
            declaration: AlRule,
            indexer: () -> Symbol
    ): KtDeclaration {
        val child = declaration.firstAsRule()
        val annotations = child
                .childrenAs(AlRuleType.MODIFIERS)
                .flatMap { it.childrenAs(AlRuleType.ANNOTATION) }

        return when (child.type) {
            AlRuleType.CLASS_DECLARATION -> parseClassDeclaration(child, annotations, indexer)
            AlRuleType.FUNCTION_DECLARATION -> parseFunctionDeclaration(child, annotations, indexer)
            AlRuleType.PROPERTY_DECLARATION -> parsePropertyDeclaration(child, annotations, indexer)
            else -> throw LineException("class or function or property declaration expected", child)
        }
    }

    private fun parseClassDeclaration(
            classDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: () -> Symbol
    ): KtDeclarationType {
        val symbol = indexer()
        val line = classDeclaration.childAs(AlTokenType.CLASS).line
        val identifier = classDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
            throw LineException("type parameters are not supported", line)
        }

        val parameters = if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
            classDeclaration
                    .childAs(AlRuleType.PRIMARY_CONSTRUCTOR)
                    .childAs(AlRuleType.CLASS_PARAMETERS)
                    .childrenAs(AlRuleType.CLASS_PARAMETER)
                    .map { parseClassParameter(it, indexer) }
        } else listOf()

        val constructorInvocation = KtConstructorInvocation(classDeclaration, line)

        val enumEntries = if (classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY)) {
            classDeclaration
                    .childAs(AlRuleType.ENUM_CLASS_BODY)
                    .childrenAs(AlRuleType.ENUM_ENTRIES)
                    .flatMap { it.childrenAs(AlRuleType.ENUM_ENTRY) }
                    .map { parseEnumEntry(it, indexer) }
        } else null

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
            val child = it.firstAsRule()
            if (child.type == AlRuleType.DECLARATION) KtDeclaration(child, indexer)
            else throw LineException("class member declaration not supported", it)
        }

        declarations.forEach {
            if (it is KtDeclarationType) {
                throw LineException("nested class declaration not permitted", it)
            }
        }

        return KtDeclarationType(
                line,
                identifier,
                symbol,
                annotations.map { KtAnnotationType(it) },
                parameters,
                constructorInvocation,
                enumEntries,
                declarations
        )
    }

    private fun parseFunctionDeclaration(
            functionDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: () -> Symbol
    ): KtDeclarationFunction {
        val symbol = indexer()
        val line = functionDeclaration.childAs(AlTokenType.FUN).line
        val identifier = functionDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()

        val parameters = functionDeclaration
                .childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
                .childrenAs(AlRuleType.FUNCTION_VALUE_PARAMETER)
                .map { parseFunctionValueParameter(it, indexer) }

        val typeIdentifier = if (functionDeclaration.containsType(AlRuleType.TYPE)) {
            KtTypeIdentifierParser.parse(functionDeclaration.childAs(AlRuleType.TYPE))
        } else "Unit"

        val block = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
            val blockOrExpression = functionDeclaration.childAs(AlRuleType.FUNCTION_BODY).firstAsRule()
            when (blockOrExpression.type) {
                AlRuleType.BLOCK -> KtBlock(blockOrExpression)
                AlRuleType.EXPRESSION -> throw LineException("function expressions are not supported", line)
                else -> throw LineException("block or expression expected", line)
            }
        } else KtBlock(line, listOf())

        return KtDeclarationFunction(
                line,
                identifier,
                symbol,
                annotations.map { KtAnnotationFunction(it) },
                parameters,
                typeIdentifier,
                block,
                null
        )
    }

    private fun parsePropertyDeclaration(
            propertyDeclaration: AlRule,
            annotations: List<AlRule>,
            indexer: () -> Symbol
    ): KtDeclarationBaseProperty {
        val symbol = indexer()
        val line = propertyDeclaration.childAs(AlTokenType.VAL).line
        if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
            throw LineException("expression assignment expected", line)
        }
        val expression = KtExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION))
        val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
        val identifier = variableDeclaration
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        if (variableDeclaration.containsType(AlRuleType.TYPE)) {
            throw LineException("explicit type declaration not supported", line)
        }
        return KtDeclarationBaseProperty(
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
            indexer: () -> Symbol
    ): KtDeclarationParameter {
        val symbol = indexer()
        val identifier = classParameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val typeIdentifier = KtTypeIdentifierParser.parse(classParameter.childAs(AlRuleType.TYPE))
        val expression = if (classParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(classParameter.childAs(AlRuleType.EXPRESSION))
        } else null
        return KtDeclarationParameter(
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
            indexer: () -> Symbol
    ): KtDeclarationParameter {
        val symbol = indexer()
        val identifier = functionValueParameter
                .childAs(AlRuleType.PARAMETER)
                .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                .firstAsTokenText()
        val typeIdentifier = KtTypeIdentifierParser.parse(functionValueParameter
                .childAs(AlRuleType.PARAMETER)
                .childAs(AlRuleType.TYPE))
        val expression = if (functionValueParameter.containsType(AlRuleType.EXPRESSION)) {
            KtExpression(functionValueParameter.childAs(AlRuleType.EXPRESSION))
        } else null
        return KtDeclarationParameter(
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
            indexer: () -> Symbol
    ): KtDeclarationEnumEntry {
        val symbol = indexer()
        val identifier = enumEntry.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val args = enumEntry
                .childrenAs(AlRuleType.VALUE_ARGUMENTS)
                .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                .map { it.childAs(AlRuleType.EXPRESSION) }
                .map { KtExpression(it) }
        val arg = when (args.size) {
            0 -> null
            1 -> args[0]
            else -> throw LineException("too many arguments in enum declaration", enumEntry)
        }

        return KtDeclarationEnumEntry(
                enumEntry.line,
                identifier,
                symbol,
                null,
                arg
        )
    }
}