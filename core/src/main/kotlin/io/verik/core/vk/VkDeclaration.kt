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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlTokenType

sealed class VkDeclaration(open val identifier: String, open val fileLine: FileLine) {

    companion object {

        operator fun invoke(declaration: AlRule): VkDeclaration {
            val child = declaration.firstAsRule()
            return when (child.type) {
                AlRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                AlRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                AlRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw FileLineException("class funtion or property expected", declaration.fileLine)
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
        override val identifier: String,
        override val fileLine: FileLine,
        val annotations: List<VkClassAnnotation>,
        val modifiers: List<VkClassModifier>,
        val delegationSpecifierName: String,
        val body: AlRule?
): VkDeclaration(identifier, fileLine) {

    companion object {

        operator fun invoke(classDeclaration: AlRule): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            val classOrInterface = when {
                classDeclaration.containsType(AlTokenType.CLASS) -> classDeclaration.childAs(AlTokenType.CLASS)
                classDeclaration.containsType(AlTokenType.INTERFACE) -> classDeclaration.childAs(AlTokenType.INTERFACE)
                else -> throw FileLineException("class or interface expected", classDeclaration.fileLine)
            }
            if (classOrInterface.type == AlTokenType.INTERFACE) {
                throw FileLineException("class interfaces are not supported", classDeclaration.fileLine)
            }

            val simpleIdentifier = classDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()
            if (identifier.length <= 1) throw FileLineException("illegal identifier", simpleIdentifier.fileLine)
            if (identifier[0] != '_') throw FileLineException("identifier must begin with an underscore", simpleIdentifier.fileLine)

            if (classDeclaration.containsType(AlRuleType.TYPE_PARAMETERS)) {
                throw FileLineException("type parameters are not supported", classDeclaration.fileLine)
            }
            if (classDeclaration.containsType(AlRuleType.PRIMARY_CONSTRUCTOR)) {
                throw FileLineException("constructor is not supported", classDeclaration.fileLine)
            }

            if (!classDeclaration.containsType(AlRuleType.DELEGATION_SPECIFIERS)) {
                throw FileLineException("delegation specifier required", classDeclaration.fileLine)
            }
            val delegationSpecifiers = classDeclaration.childAs(AlRuleType.DELEGATION_SPECIFIERS)
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.directDescendantAs(AlRuleType.SIMPLE_IDENTIFIER,
                    FileLineException("delegation specifier not supported", classDeclaration.fileLine))
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

            return VkClassDeclaration(identifier, classOrInterface.fileLine, annotations, modifiers, delegationSpecifierName, body)
        }
    }
}

data class VkFunctionDeclaration (
        override val identifier: String,
        override val fileLine: FileLine,
        val annotations: List<VkFunctionAnnotation>,
        val modifiers: List<VkFunctionModifier>,
        val body: AlRule?
): VkDeclaration(identifier, fileLine) {

    companion object {

        operator fun invoke(functionDeclaration: AlRule): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val function = functionDeclaration.childAs(AlTokenType.FUN)

            val simpleIdentifier = functionDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()

            val functionValueParameters = functionDeclaration.childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
            if (functionValueParameters.children.isNotEmpty()) {
                throw FileLineException("function value parameters not supported", functionValueParameters.fileLine)
            }

            if (functionDeclaration.containsType(AlRuleType.TYPE)) {
                throw FileLineException("function return type not supported", functionValueParameters.fileLine)
            }

            val body = if (functionDeclaration.containsType(AlRuleType.FUNCTION_BODY)) {
                functionDeclaration.childAs(AlRuleType.FUNCTION_BODY)
            } else null

            return VkFunctionDeclaration(identifier, function.fileLine, annotations, modifiers, body)
        }
    }
}

data class VkPropertyDeclaration(
        override val identifier: String,
        override val fileLine: FileLine,
        val annotations: List<VkPropertyAnnotation>,
        val expression: VkExpression
): VkDeclaration(identifier, fileLine) {

    companion object {

        operator fun invoke(propertyDeclaration: AlRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            getModifiers(propertyDeclaration) {
                throw FileLineException("illegal property modifier", it.fileLine)
            }

            val value = propertyDeclaration.childAs(AlTokenType.VAL)

            val variableDeclaration = propertyDeclaration.childAs(AlRuleType.VARIABLE_DECLARATION)
            if (variableDeclaration.containsType(AlRuleType.TYPE)) {
                throw FileLineException("type declaration not permitted here", propertyDeclaration.fileLine)
            }
            val identifier = variableDeclaration.firstAsRule().firstAsTokenText()
            if (identifier.isEmpty()) {
                throw FileLineException("illegal variable identifier", variableDeclaration.fileLine)
            }
            if (identifier[0] == '_') {
                throw FileLineException("variable identifier must not begin with an underscore", variableDeclaration.fileLine)
            }

            if (!propertyDeclaration.containsType(AlRuleType.EXPRESSION)) {
                throw FileLineException("type declaration expected", propertyDeclaration.fileLine)
            }
            val expression = VkExpression(propertyDeclaration.childAs(AlRuleType.EXPRESSION))

            return VkPropertyDeclaration(identifier, value.fileLine, annotations, expression)
        }
    }
}
