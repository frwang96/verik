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

sealed class KtDeclaration(
        open val identifier: String,
        open val modifiers: List<KtModifier>,
        open val fileLine: FileLine
) {

    companion object {

        operator fun invoke(declaration: AlRule): KtDeclaration {
            val child = declaration.firstAsRule()
            val modifiers = child
                    .childrenAs(AlRuleType.MODIFIERS)
                    .flatMap { it.children }
                    .map { it.asRule() }
                    .mapNotNull { KtModifier(it) }
            return when (child.type) {
                AlRuleType.CLASS_DECLARATION -> KtDeclarationClass(child, modifiers)
                AlRuleType.FUNCTION_DECLARATION -> KtDeclarationFunction(child, modifiers)
                AlRuleType.PROPERTY_DECLARATION -> KtDeclarationProperty(child, modifiers)
                else -> throw FileLineException("class or function or property expected", declaration.fileLine)
            }
        }
    }
}

data class KtDeclarationClass(
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        override val fileLine: FileLine,
        val parameters: List<KtParameter>,
        val delegationSpecifier: KtDelegationSpecifier,
        val enumEntries: List<KtEnumEntry>?,
        val declarations: List<KtDeclaration>
): KtDeclaration(identifier, modifiers, fileLine) {

    companion object {

        operator fun invoke(classDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationClass {
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
                        .map { KtParameter(it) }
            } else listOf()

            val delegationSpecifiers = classDeclaration
                    .childrenAs(AlRuleType.DELEGATION_SPECIFIERS)
                    .flatMap { it.childrenAs(AlRuleType.ANNOTATED_DELEGATION_SPECIFIER) }
                    .map { it.childAs(AlRuleType.DELEGATION_SPECIFIER) }
                    .map { KtDelegationSpecifier(it) }
            if (delegationSpecifiers.isEmpty()) {
                throw FileLineException("delegation specifier expected", fileLine)
            }
            if (delegationSpecifiers.size > 1) {
                throw FileLineException("multiple delegation specifiers not supported", fileLine)
            }

            val enumEntries = if (classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY)) {
                classDeclaration
                        .childAs(AlRuleType.ENUM_CLASS_BODY)
                        .childrenAs(AlRuleType.ENUM_ENTRIES)
                        .flatMap { it.childrenAs(AlRuleType.ENUM_ENTRY) }
                        .map { KtEnumEntry(it) }
            } else null

            val declarations = when {
                classDeclaration.containsType(AlRuleType.CLASS_BODY) -> {
                    classDeclaration
                            .childAs(AlRuleType.CLASS_BODY)
                            .childAs(AlRuleType.CLASS_MEMBER_DECLARATIONS)
                            .childrenAs(AlRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.childAs(AlRuleType.DECLARATION) }
                            .map { KtDeclaration(it) }
                }
                classDeclaration.containsType(AlRuleType.ENUM_CLASS_BODY) -> {
                    classDeclaration
                            .childAs(AlRuleType.ENUM_CLASS_BODY)
                            .childrenAs(AlRuleType.CLASS_MEMBER_DECLARATIONS)
                            .flatMap { it.childrenAs(AlRuleType.CLASS_MEMBER_DECLARATION) }
                            .map { it.childAs(AlRuleType.DECLARATION) }
                            .map { KtDeclaration(it) }
                }
                else -> listOf()
            }

            return KtDeclarationClass(
                    identifier,
                    modifiers,
                    fileLine,
                    parameters,
                    delegationSpecifiers[0],
                    enumEntries,
                    declarations
            )
        }
    }
}

data class KtDeclarationFunction(
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        override val fileLine: FileLine,
        val parameters: List<KtParameter>,
        val typeIdentifier: String?,
        val block: KtBlock,
        var type: KtType?
): KtDeclaration(identifier, modifiers, fileLine) {

    companion object {

        operator fun invoke(functionDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationFunction {
            val fileLine = functionDeclaration.childAs(AlTokenType.FUN).fileLine
            val identifier = functionDeclaration
                    .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                    .firstAsTokenText()
            val parameters = functionDeclaration
                    .childAs(AlRuleType.FUNCTION_VALUE_PARAMETERS)
                    .childrenAs(AlRuleType.FUNCTION_VALUE_PARAMETER)
                    .map { KtParameter(it) }

            val typeIdentifier = if (functionDeclaration.containsType(AlRuleType.TYPE)) {
                KtType.identifier(functionDeclaration.childAs(AlRuleType.TYPE))
            } else null

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
    }

}

data class KtDeclarationProperty(
        override val identifier: String,
        override val modifiers: List<KtModifier>,
        override val fileLine: FileLine,
        val expression: KtExpression,
        var type: KtType?
): KtDeclaration(identifier, modifiers, fileLine) {

    companion object {

        operator fun invoke(propertyDeclaration: AlRule, modifiers: List<KtModifier>): KtDeclarationProperty {
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
            return KtDeclarationProperty(identifier, modifiers, fileLine, expression, null)
        }
    }

}
