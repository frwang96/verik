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
import io.verik.core.kt.KtRule
import io.verik.core.kt.KtRuleType
import io.verik.core.kt.KtTokenType

sealed class VkDeclaration(open val identifier: String, open val fileLine: FileLine) {

    companion object {

        operator fun invoke(declaration: KtRule): VkDeclaration {
            val child = declaration.firstAsRule()
            return when (child.type) {
                KtRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                KtRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                KtRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw FileLineException("class funtion or property expected", declaration.fileLine)
            }
        }

        fun <T> getAnnotations(declaration: KtRule, map: (KtRule) -> T): List<T> {
            return declaration.childrenAs(KtRuleType.MODIFIERS).flatMap { it.childrenAs(KtRuleType.ANNOTATION) }.map(map)
        }

        fun <T> getModifiers(declaration: KtRule, map: (KtRule) -> T): List<T> {
            return declaration.childrenAs(KtRuleType.MODIFIERS).flatMap { it.childrenAs(KtRuleType.MODIFIER) }.map(map)
        }
    }
}


data class VkClassDeclaration(
        override val identifier: String,
        override val fileLine: FileLine,
        val annotations: List<VkClassAnnotation>,
        val modifiers: List<VkClassModifier>,
        val delegationSpecifierName: String,
        val body: KtRule?
): VkDeclaration(identifier, fileLine) {

    companion object {

        operator fun invoke(classDeclaration: KtRule): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            val classOrInterface = when {
                classDeclaration.containsType(KtTokenType.CLASS) -> classDeclaration.childAs(KtTokenType.CLASS)
                classDeclaration.containsType(KtTokenType.INTERFACE) -> classDeclaration.childAs(KtTokenType.INTERFACE)
                else -> throw FileLineException("class or interface expected", classDeclaration.fileLine)
            }
            if (classOrInterface.type == KtTokenType.INTERFACE) {
                throw FileLineException("class interfaces are not supported", classDeclaration.fileLine)
            }

            val simpleIdentifier = classDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()
            if (identifier.length <= 1) throw FileLineException("illegal identifier", simpleIdentifier.fileLine)
            if (identifier[0] != '_') throw FileLineException("identifier must begin with an underscore", simpleIdentifier.fileLine)
            if (identifier.endsWith("_mirror")) throw FileLineException("identifier $identifier is reserved", simpleIdentifier.fileLine)

            if (classDeclaration.containsType(KtRuleType.TYPE_PARAMETERS)) {
                throw FileLineException("type parameters are not supported", classDeclaration.fileLine)
            }
            if (classDeclaration.containsType(KtRuleType.PRIMARY_CONSTRUCTOR)) {
                throw FileLineException("constructor is not supported", classDeclaration.fileLine)
            }

            if (!classDeclaration.containsType(KtRuleType.DELEGATION_SPECIFIERS)) {
                throw FileLineException("delegation specifier required", classDeclaration.fileLine)
            }
            val delegationSpecifiers = classDeclaration.childAs(KtRuleType.DELEGATION_SPECIFIERS)
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.directDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    FileLineException("delegation specifier not supported", classDeclaration.fileLine))
            val delegationSpecifierName = delegationSpecifierSimpleIdentifier.firstAsTokenText()

            val body = when {
                classDeclaration.containsType(KtRuleType.CLASS_BODY) -> {
                    classDeclaration.childAs(KtRuleType.CLASS_BODY)
                }
                classDeclaration.containsType(KtRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration.childAs(KtRuleType.ENUM_CLASS_BODY)
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
        val body: KtRule?
): VkDeclaration(identifier, fileLine) {

    companion object {

        operator fun invoke(functionDeclaration: KtRule): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val function = functionDeclaration.childAs(KtTokenType.FUN)

            val simpleIdentifier = functionDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()

            val functionValueParameters = functionDeclaration.childAs(KtRuleType.FUNCTION_VALUE_PARAMETERS)
            if (functionValueParameters.children.isNotEmpty()) {
                throw FileLineException("function value parameters not supported", functionValueParameters.fileLine)
            }

            if (functionDeclaration.containsType(KtRuleType.TYPE)) {
                throw FileLineException("function return type not supported", functionValueParameters.fileLine)
            }

            val body = if (functionDeclaration.containsType(KtRuleType.FUNCTION_BODY)) {
                functionDeclaration.childAs(KtRuleType.FUNCTION_BODY)
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

        operator fun invoke(propertyDeclaration: KtRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            getModifiers(propertyDeclaration) {
                throw FileLineException("illegal property modifier", it.fileLine)
            }

            val value = propertyDeclaration.childAs(KtTokenType.VAL)

            val variableDeclaration = propertyDeclaration.childAs(KtRuleType.VARIABLE_DECLARATION)
            if (variableDeclaration.containsType(KtRuleType.TYPE)) {
                throw FileLineException("type declaration not permitted here", propertyDeclaration.fileLine)
            }
            val identifier = variableDeclaration.firstAsRule().firstAsTokenText()
            if (identifier.isEmpty()) {
                throw FileLineException("illegal variable identifier", variableDeclaration.fileLine)
            }
            if (identifier[0] == '_') {
                throw FileLineException("variable identifier must not begin with an underscore", variableDeclaration.fileLine)
            }

            if (!propertyDeclaration.containsType(KtRuleType.EXPRESSION)) {
                throw FileLineException("type declaration expected", propertyDeclaration.fileLine)
            }
            val expression = VkExpression(propertyDeclaration.childAs(KtRuleType.EXPRESSION))

            return VkPropertyDeclaration(identifier, value.fileLine, annotations, expression)
        }
    }
}
