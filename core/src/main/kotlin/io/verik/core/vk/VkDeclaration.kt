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

import io.verik.core.LinePos
import io.verik.core.LinePosException
import io.verik.core.kt.KtRule
import io.verik.core.kt.KtRuleType
import io.verik.core.kt.KtTokenType

sealed class VkDeclaration(open val identifier: String, open val linePos: LinePos) {

    companion object {

        operator fun invoke(declaration: KtRule): VkDeclaration {
            val child = declaration.firstAsRule()
            return when (child.type) {
                KtRuleType.CLASS_DECLARATION -> VkClassDeclaration(child)
                KtRuleType.FUNCTION_DECLARATION -> VkFunctionDeclaration(child)
                KtRuleType.PROPERTY_DECLARATION -> VkPropertyDeclaration(child)
                else -> throw LinePosException("class funtion or property expected", declaration.linePos)
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
        override val linePos: LinePos,
        val annotations: List<VkClassAnnotation>,
        val modifiers: List<VkClassModifier>,
        val delegationSpecifierName: String,
        val body: KtRule?
): VkDeclaration(identifier, linePos) {

    companion object {

        operator fun invoke(classDeclaration: KtRule): VkClassDeclaration {
            val annotations = getAnnotations(classDeclaration) { VkClassAnnotation(it) }
            val modifiers = getModifiers(classDeclaration) { VkClassModifier(it) }

            val classOrInterface = when {
                classDeclaration.containsType(KtTokenType.CLASS) -> classDeclaration.childAs(KtTokenType.CLASS)
                classDeclaration.containsType(KtTokenType.INTERFACE) -> classDeclaration.childAs(KtTokenType.INTERFACE)
                else -> throw LinePosException("class or interface expected", classDeclaration.linePos)
            }
            if (classOrInterface.type == KtTokenType.INTERFACE) {
                throw LinePosException("class interfaces are not supported", classDeclaration.linePos)
            }

            val simpleIdentifier = classDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()
            if (identifier.length <= 1) throw LinePosException("illegal identifier", simpleIdentifier.linePos)
            if (identifier[0] != '_') throw LinePosException("identifier must begin with an underscore", simpleIdentifier.linePos)

            if (classDeclaration.containsType(KtRuleType.TYPE_PARAMETERS)) {
                throw LinePosException("type parameters are not supported", classDeclaration.linePos)
            }
            if (classDeclaration.containsType(KtRuleType.PRIMARY_CONSTRUCTOR)) {
                throw LinePosException("constructor is not supported", classDeclaration.linePos)
            }

            if (!classDeclaration.containsType(KtRuleType.DELEGATION_SPECIFIERS)) {
                throw LinePosException("delegation specifier required", classDeclaration.linePos)
            }
            val delegationSpecifiers = classDeclaration.childAs(KtRuleType.DELEGATION_SPECIFIERS)
            val delegationSpecifierSimpleIdentifier = delegationSpecifiers.directDescendantAs(KtRuleType.SIMPLE_IDENTIFIER,
                    LinePosException("delegation specifier not supported", classDeclaration.linePos))
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

            return VkClassDeclaration(identifier, classOrInterface.linePos, annotations, modifiers, delegationSpecifierName, body)
        }
    }
}

data class VkFunctionDeclaration (
        override val identifier: String,
        override val linePos: LinePos,
        val annotations: List<VkFunctionAnnotation>,
        val modifiers: List<VkFunctionModifier>,
        val body: KtRule?
): VkDeclaration(identifier, linePos) {

    companion object {

        operator fun invoke(functionDeclaration: KtRule): VkFunctionDeclaration {
            val annotations = getAnnotations(functionDeclaration) { VkFunctionAnnotation(it) }
            val modifiers = getModifiers(functionDeclaration) { VkFunctionModifier(it) }

            val function = functionDeclaration.childAs(KtTokenType.FUN)

            val simpleIdentifier = functionDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER)
            val identifier = simpleIdentifier.firstAsTokenText()

            val functionValueParameters = functionDeclaration.childAs(KtRuleType.FUNCTION_VALUE_PARAMETERS)
            if (functionValueParameters.children.isNotEmpty()) {
                throw LinePosException("function value parameters not supported", functionValueParameters.linePos)
            }

            if (functionDeclaration.containsType(KtRuleType.TYPE)) {
                throw LinePosException("function return type not supported", functionValueParameters.linePos)
            }

            val body = if (functionDeclaration.containsType(KtRuleType.FUNCTION_BODY)) {
                functionDeclaration.childAs(KtRuleType.FUNCTION_BODY)
            } else null

            return VkFunctionDeclaration(identifier, function.linePos, annotations, modifiers, body)
        }
    }
}

data class VkPropertyDeclaration(
        override val identifier: String,
        override val linePos: LinePos,
        val annotations: List<VkPropertyAnnotation>,
        val expression: VkExpression
): VkDeclaration(identifier, linePos) {

    companion object {

        operator fun invoke(propertyDeclaration: KtRule): VkPropertyDeclaration {
            val annotations = getAnnotations(propertyDeclaration) { VkPropertyAnnotation(it) }
            getModifiers(propertyDeclaration) {
                throw LinePosException("illegal property modifier", it.linePos)
            }

            val value = propertyDeclaration.childAs(KtTokenType.VAL)

            val variableDeclaration = propertyDeclaration.childAs(KtRuleType.VARIABLE_DECLARATION)
            if (variableDeclaration.containsType(KtRuleType.TYPE)) {
                throw LinePosException("type declaration not permitted here", propertyDeclaration.linePos)
            }
            val identifier = variableDeclaration.firstAsRule().firstAsTokenText()
            if (identifier.isEmpty()) {
                throw LinePosException("illegal variable identifier", variableDeclaration.linePos)
            }
            if (identifier[0] == '_') {
                throw LinePosException("variable identifier must not begin with an underscore", variableDeclaration.linePos)
            }

            if (!propertyDeclaration.containsType(KtRuleType.EXPRESSION)) {
                throw LinePosException("type declaration expected", propertyDeclaration.linePos)
            }
            val expression = VkExpression(propertyDeclaration.childAs(KtRuleType.EXPRESSION))

            return VkPropertyDeclaration(identifier, value.linePos, annotations, expression)
        }
    }
}
