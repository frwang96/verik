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

package io.verik.core

import io.verik.core.kt.KtRule
import io.verik.core.kt.KtRuleType
import io.verik.core.kt.KtTokenType

enum class HeaderDeclarationType {
    ENUM,
    CLASS
}

data class HeaderDeclaration(
        val type: HeaderDeclarationType,
        val name: String
)

class HeaderParser {

    companion object {

        fun parse(kotlinFile: KtRule): List<HeaderDeclaration> {
            val classDeclarations = kotlinFile.childrenAs(KtRuleType.TOP_LEVEL_OBJECT)
                    .map { it.childAs(KtRuleType.DECLARATION).firstAsRule() }
                    .filter { it.type == KtRuleType.CLASS_DECLARATION }
            return classDeclarations.mapNotNull { getHeaderDeclaration(it) }
        }

        private fun getHeaderDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            val underscoredName = classDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
            return if (underscoredName[0] == '_') {
                val name = underscoredName.substring(1)
                when {
                    isEnumDeclaration(classDeclaration) -> HeaderDeclaration(HeaderDeclarationType.ENUM, name)
                    isClassDeclaration(classDeclaration) -> HeaderDeclaration(HeaderDeclarationType.CLASS, name)
                    else -> null
                }
            } else null
        }

        private fun isEnumDeclaration(classDeclaration: KtRule): Boolean {
            return if (classDeclaration.containsType(KtRuleType.MODIFIERS)) {
                val modifiers = classDeclaration.childAs(KtRuleType.MODIFIERS).childrenAs(KtRuleType.MODIFIER)
                val classModifiers = modifiers.map { it.firstAsRule() }.filter { it.type == KtRuleType.CLASS_MODIFIER }
                classModifiers.any { it.firstAsTokenType() == KtTokenType.ENUM }
            } else false
        }

        private fun isClassDeclaration(classDeclaration: KtRule): Boolean {
            return if (classDeclaration.containsType(KtRuleType.DELEGATION_SPECIFIERS)
                    && classDeclaration.containsType(KtRuleType.CLASS_BODY)) {
                val delegationSpecifiers = classDeclaration.childAs(KtRuleType.DELEGATION_SPECIFIERS)
                        .childrenAs(KtRuleType.ANNOTATED_DELEGATION_SPECIFIER)
                        .map { it.childAs(KtRuleType.DELEGATION_SPECIFIER) }
                val userTypes = delegationSpecifiers
                        .map { it.firstAsRule() }
                        .filter { it.type == KtRuleType.USER_TYPE }
                val simpleUserTypes = userTypes
                        .filter { it.children.size == 1 }
                        .map { it.childAs(KtRuleType.SIMPLE_USER_TYPE) }
                val simpleIdenfitiers = simpleUserTypes
                        .filter { !it.containsType(KtRuleType.TYPE_ARGUMENTS) }
                        .map { it.childAs(KtRuleType.SIMPLE_IDENTIFIER) }
                        .map { it.firstAsTokenText() }
                return simpleIdenfitiers.none { it in listOf("_module", "_intf", "_enum", "_struct") }
            } else false
        }
    }
}
