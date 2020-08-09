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

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlTokenType

class KtDeclarationParser {

    companion object {

        fun parseDeclarationType(classDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationType {
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
                        .map { parseParameter(it) }
            } else listOf()

            val parentExpression = parseParentExpression(classDeclaration, line)

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
                else throw LineException("class member declaration not supported", it)
            }

            return KtDeclarationType(
                    line,
                    identifier,
                    modifiers,
                    parameters,
                    parentExpression,
                    enumEntries,
                    declarations,
                    null
            )
        }

        fun parseDeclarationFunction(functionDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationFunction {
            val line = functionDeclaration.childAs(AlTokenType.FUN).line
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
                    AlRuleType.EXPRESSION -> throw LineException("function expressions are not supported", line)
                    else -> throw LineException("block or expression expected", line)
                }
            } else KtBlock(line, listOf())

            return KtDeclarationFunction(
                    line,
                    identifier,
                    modifiers,
                    parameters,
                    typeIdentifier,
                    block,
                    null
            )
        }

        fun parseDeclarationProperty(propertyDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationProperty {
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
            return KtDeclarationProperty(line, identifier, modifiers, expression)
        }

        private fun parseParameter(parameter: AlRule): KtDeclarationProperty {
            return when (parameter.type) {
                AlRuleType.CLASS_PARAMETER -> {
                    val identifier = parameter.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                    val typeIdentifier = KtTypeIdentifierParser.parse(parameter.childAs(AlRuleType.TYPE))
                    val expression = if (parameter.containsType(AlRuleType.EXPRESSION)) {
                        KtExpression(parameter.childAs(AlRuleType.EXPRESSION))
                    } else null
                    parseParameter(identifier, typeIdentifier, expression, parameter.line)
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
                    parseParameter(identifier, typeIdentifier, expression, parameter.line)
                }
                else -> throw LineException("class parameter or function value parameter expected", parameter.line)
            }
        }

        private fun parseParameter(
                identifier: String,
                typeIdentifier: String,
                expression: KtExpression?,
                line: Int
        ): KtDeclarationProperty {
            return if (expression != null) {
                KtDeclarationProperty(
                        line,
                        identifier,
                        listOf(),
                        expression
                )
            } else {
                KtDeclarationProperty(
                        line,
                        identifier,
                        listOf(),
                        KtExpressionFunction(line, null, KtFunctionIdentifierNamed(typeIdentifier, false), listOf())
                )
            }
        }

        private fun parseParentExpression(classDeclaration: AlRule, line: Int): KtExpressionFunction {
            val delegationSpecifiers = classDeclaration
                    .childrenAs(AlRuleType.DELEGATION_SPECIFIERS)
                    .flatMap { it.childrenAs(AlRuleType.ANNOTATED_DELEGATION_SPECIFIER) }
                    .map { it.childAs(AlRuleType.DELEGATION_SPECIFIER) }
            if (delegationSpecifiers.isEmpty()) {
                throw LineException("parent type expected", line)
            }
            if (delegationSpecifiers.size > 1) {
                throw LineException("multiple parent types are not permitted", line)
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
                            child.line,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            args
                    )
                }
                AlRuleType.USER_TYPE -> {
                    val typeIdentifier = KtTypeIdentifierParser.parse(child)
                    KtExpressionFunction(
                            child.line,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            listOf()
                    )
                }
                else -> throw LineException("constructor invocation or user type expected", line)
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
                    enumEntry.line,
                    identifier,
                    listOf(),
                    KtExpressionFunction(
                            enumEntry.line,
                            null,
                            KtFunctionIdentifierNamed(typeIdentifier, false),
                            args
                    )
            )
        }
    }
}