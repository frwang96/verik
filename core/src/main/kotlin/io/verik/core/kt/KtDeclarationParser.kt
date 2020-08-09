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

package io.verik.core.kt

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlTokenType

class KtDeclarationParser {

    companion object {

        fun parseDeclarationType(classDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationType {
            val fileLine = classDeclaration.childAs(AlTokenType.CLASS).fileLine
            val identifier = classDeclaration
                    .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .firstAsTokenText()
            if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
                throw FileLineException("type parameters are not supported", fileLine)
            }

            val parameters = if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
                classDeclaration
                        .childAs(AlRuleType.PRIMARY_CONSTRUCTOR)
                        .childAs(AlRuleType.CLASS_PARAMETERS)
                        .childrenAs(AlRuleType.CLASS_PARAMETER)
                        .map { parseParameter(it) }
            } else listOf()

            val parentExpression = parseParentExpression(classDeclaration, fileLine)

            val enumEntries = if (classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY)) {
                classDeclaration
                        .childAs(AlRuleType.ENUM_CLASS_BODY)
                        .childrenAs(AlRuleType.ENUM_ENTRIES)
                        .flatMap { it.childrenAs(AlRuleType.ENUM_ENTRY) }
                        .map { parseEnumEntry(it, identifier) }
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
                val child = it.firstAsRule()
                if (child.type == AlRuleType.DECLARATION) KtDeclaration(child)
                else throw FileLineException("class member declaration not supported", it.fileLine)
            }

            return KtDeclarationType(
                    identifier,
                    modifiers,
                    fileLine,
                    parameters,
                    parentExpression,
                    enumEntries,
                    declarations,
                    null
            )
        }

        fun parseDeclarationFunction(functionDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationFunction {
            val fileLine = functionDeclaration.childAs(AlTokenType.FUN).fileLine
            val identifier = functionDeclaration
                    .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .firstAsTokenText()
            val parameters = functionDeclaration
                    .childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
                    .childrenAs(AlRuleType.FUNCTION_VALUE_PARAMETER)
                    .map { parseParameter(it) }

            val typeIdentifier = if (functionDeclaration.containsType(AlRuleType.TYPE)) {
                KtTypeIdentifierParser.parse(functionDeclaration.childAs(AlRuleType.TYPE))
            } else "Unit"

            val block = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
                val blockOrExpression = functionDeclaration.childAs(AlRuleType.FUNCTION_BODY).firstAsRule()
                when (blockOrExpression.type) {
                    AlRuleType.BLOCK -> KtBlock(blockOrExpression)
                    AlRuleType.EXPRESSION -> throw FileLineException("function expressions are not supported", fileLine)
                    else -> throw FileLineException("block or expression expected", fileLine)
                }
            } else KtBlock(listOf(), fileLine)

            return KtDeclarationFunction(
                    identifier,
                    modifiers,
                    fileLine,
                    parameters,
                    typeIdentifier,
                    block,
                    null
            )
        }

        fun parseDeclarationProperty(propertyDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationProperty {
            val fileLine = propertyDeclaration.childAs(AlTokenType.VAL).fileLine
            if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
                throw FileLineException("expression assignment expected", fileLine)
            }
            val expression = KtExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION))
            val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
            val identifier = variableDeclaration
                    .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .firstAsTokenText()
            if (variableDeclaration.containsType(AlRuleType.TYPE)) {
                throw FileLineException("explicit type declaration not supported", fileLine)
            }
            return KtDeclarationProperty(identifier, modifiers, fileLine, expression)
        }

        private fun parseParameter(parameter: AlRule): KtDeclarationProperty {
            return when (parameter.type) {
                AlRuleType.CLASS_PARAMETER -> {
                    val identifier = parameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                    val typeIdentifier = KtTypeIdentifierParser.parse(parameter.childAs(AlRuleType.TYPE))
                    val expression = if (parameter.containsType(AlRuleType.EXPRESSION)) {
                        KtExpression(parameter.childAs(AlRuleType.EXPRESSION))
                    } else null
                    parseParameter(identifier, typeIdentifier, expression, parameter.fileLine)
                }
                AlRuleType.FUNCTION_VALUE_PARAMETER -> {
                    val identifier = parameter
                            .childAs(AlRuleType.PARAMETER)
                            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                            .firstAsTokenText()
                    val typeIdentifier = KtTypeIdentifierParser.parse(parameter
                            .childAs(AlRuleType.PARAMETER)
                            .childAs(AlRuleType.TYPE))
                    val expression = if (parameter.containsType(AlRuleType.EXPRESSION)) {
                        KtExpression(parameter.childAs(AlRuleType.EXPRESSION))
                    } else null
                    parseParameter(identifier, typeIdentifier, expression, parameter.fileLine)
                }
                else -> throw FileLineException("class parameter or function value parameter expected", parameter.fileLine)
            }
        }

        private fun parseParameter(
                identifier: String,
                typeIdentifier: String,
                expression: KtExpression?,
                fileLine: FileLine
        ): KtDeclarationProperty {
            return if (expression != null) {
                KtDeclarationProperty(
                        identifier,
                        listOf(),
                        fileLine,
                        expression
                )
            } else {
                KtDeclarationProperty(
                        identifier,
                        listOf(),
                        fileLine,
                        KtExpressionFunction(fileLine, null, KtFunctionIdentifierNamed(typeIdentifier, false), listOf())
                )
            }
        }

        private fun parseParentExpression(classDeclaration: AlRule, fileLine: FileLine): KtExpressionFunction {
            val delegationSpecifiers = classDeclaration
                    .childrenAs(AlRuleType.DELEGATION_SPECIFIERS)
                    .flatMap { it.childrenAs(AlRuleType.ANNOTATED_DELEGATION_SPECIFIER) }
                    .map { it.childAs(AlRuleType.DELEGATION_SPECIFIER) }
            if (delegationSpecifiers.isEmpty()) {
                throw FileLineException("parent type expected", fileLine)
            }
            if (delegationSpecifiers.size > 1) {
                throw FileLineException("multiple parent types are not permitted", fileLine)
            }
            val child = delegationSpecifiers[0].firstAsRule()
            return when (child.type) {
                AlRuleType.CONSTRUCTOR_INVOCATION -> {
                    val typeIdentifier = KtTypeIdentifierParser.parse(child.childAs(AlRuleType.USER_TYPE))
                    val args = child
                            .childAs(AlRuleType.VALUE_ARGUMENTS)
                            .childrenAs(AlRuleType.VALUE_ARGUMENT)
                            .map { it.childAs(AlRuleType.EXPRESSION) }
                            .map { KtExpression(it) }
                    KtExpressionFunction(
                            child.fileLine,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            args
                    )
                }
                AlRuleType.USER_TYPE -> {
                    val typeIdentifier = KtTypeIdentifierParser.parse(child)
                    KtExpressionFunction(
                            child.fileLine,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            listOf()
                    )
                }
                else -> throw FileLineException("constructor invocation or user type expected", fileLine)
            }
        }

        private fun parseEnumEntry(enumEntry: AlRule, typeIdentifier: String): KtDeclarationProperty {
            val identifier = enumEntry.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
            val args = enumEntry
                    .childrenAs(AlRuleType.VALUE_ARGUMENTS)
                    .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                    .map { it.childAs(AlRuleType.EXPRESSION) }
                    .map { KtExpression(it) }
            return KtDeclarationProperty(
                    identifier,
                    listOf(),
                    enumEntry.fileLine,
                    KtExpressionFunction(
                            enumEntry.fileLine,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            args
                    )
            )
        }
    }
}