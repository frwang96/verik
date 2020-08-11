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

package io.verik.core.vk

import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlTokenType

sealed class VkDeclaration(
        override val line: Int,
        open val identifier: String
): Line {

    companion object {

        operator fun invoke(declaration: AlRule): VkDeclaration {
            val child = declaration.firstAsRule()
            return when (child.type) {
                AlRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                AlRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                AlRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw LineException("class funtion or property expected", declaration)
            }
        }

        fun <T> getAnnotations(declaration: AlRule, map: (AlRule) -> T): List<T> {
            return declaration.childrenAs(AlRuleType.MODIFIERS).flatMap { it.childrenAs(AlRuleType.ANNOTATION) }.map(map)
        }

        fun <T> getModifiers(declaration: AlRule, map: (AlRule) -> T): List<T> {
            return declaration.childrenAs(AlRuleType.MODIFIERS).flatMap { it.childrenAs(AlRuleType.MODIFIER) }.map(map)
        }
    }
}


data class VkClassDeclaration(
        override val line: Int,
        override val identifier: String,
        val annotations: List<VkClassAnnotation>,
        val modifiers: List<VkClassModifier>,
        val delegationSpecifierName: String,
        val body: AlRule?
): VkDeclaration(line, identifier) {

    companion object {

        operator fun invoke(classDeclaration: AlRule): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            val simpleIdentifier = classDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()
            if (identifier.length <= 1) throw LineException("illegal identifier", simpleIdentifier)
            if (identifier[0] != '_') throw LineException("identifier must begin with an underscore", simpleIdentifier)

            if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
                throw LineException("type parameters are not supported", classDeclaration)
            }
            if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
                throw LineException("constructor is not supported", classDeclaration)
            }

            if (!classDeclaration.containsType(AlRuleType.DELEGATION_SPECIFIERS)) {
                throw LineException("delegation specifier required", classDeclaration)
            }
            val delegationSpecifiers = classDeclaration.childAs(AlRuleType.DELEGATION_SPECIFIERS)
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.directDescendantAs(AlRuleType.SIMPLE_IDENTIFIER,
                    LineException("delegation specifier not supported", classDeclaration))
            val delegationSpecifierName = delegationSpecifierSimpleIdentifier.firstAsTokenText()

            val body = when {
                classDeclaration.containsType(AlRuleType.CLASS_BODY) -> {
                    classDeclaration.childAs(AlRuleType.CLASS_BODY)
                }
                classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration.childAs(AlRuleType.ENUM_CLASS_BODY)
                }
                else -> null
            }

            return VkClassDeclaration(
                    classDeclaration.childAs(AlTokenType.CLASS).line,
                    identifier,
                    annotations,
                    modifiers,
                    delegationSpecifierName,
                    body
            )
        }
    }
}

data class VkFunctionDeclaration (
        override val line: Int,
        override val identifier: String,
        val annotations: List<VkFunctionAnnotation>,
        val modifiers: List<VkFunctionModifier>,
        val body: AlRule?
): VkDeclaration(line, identifier) {

    companion object {

        operator fun invoke(functionDeclaration: AlRule): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val function = functionDeclaration.childAs(AlTokenType.FUN)

            val simpleIdentifier = functionDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()

            val functionValueParameters = functionDeclaration.childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
            if (functionValueParameters.children.isNotEmpty()) {
                throw LineException("function value parameters not supported", functionValueParameters)
            }

            if (functionDeclaration.containsType(AlRuleType.TYPE)) {
                throw LineException("function return type not supported", functionValueParameters)
            }

            val body = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
                functionDeclaration.childAs(AlRuleType.FUNCTION_BODY)
            } else null

            return VkFunctionDeclaration(function.line, identifier, annotations, modifiers, body)
        }
    }
}

data class VkPropertyDeclaration(
        override val line: Int,
        override val identifier: String,
        val annotations: List<VkPropertyAnnotation>,
        val expression: VkExpression
): VkDeclaration(line, identifier) {

    companion object {

        operator fun invoke(propertyDeclaration: AlRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            getModifiers(propertyDeclaration) {
                throw LineException("illegal property modifier", it)
            }

            val value = propertyDeclaration.childAs(AlTokenType.VAL)

            val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
            if (variableDeclaration.containsType(AlRuleType.TYPE)) {
                throw LineException("type declaration not permitted here", propertyDeclaration)
            }
            val identifier = variableDeclaration.firstAsRule().firstAsTokenText()
            if (identifier.isEmpty()) {
                throw LineException("illegal variable identifier", variableDeclaration)
            }
            if (identifier[0] == '_') {
                throw LineException("variable identifier must not begin with an underscore", variableDeclaration)
            }

            if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
                throw LineException("type declaration expected", propertyDeclaration)
            }
            val expression = VkExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION))

            return VkPropertyDeclaration(value.line, identifier, annotations, expression)
        }
    }
}
