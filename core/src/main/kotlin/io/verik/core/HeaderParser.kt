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

sealed class HeaderDeclaration(open val name: String)

data class HeaderDeclarationInterf(override val name: String, val modports: List<String>): HeaderDeclaration(name)

data class HeaderDeclarationClass(override val name: String, val isBaseClass: Boolean): HeaderDeclaration(name)

data class HeaderDeclarationEnum(override val name: String): HeaderDeclaration(name)

data class HeaderDeclarationStruct(override val name: String): HeaderDeclaration(name)

class HeaderParser {

    companion object {

        fun parse(kotlinFile: KtRule): List<HeaderDeclaration> {
            val classDeclarations = kotlinFile.childrenAs(KtRuleType.TOP_LEVEL_OBJECT)
                    .map { it.childAs(KtRuleType.DECLARATION).firstAsRule() }
                    .filter { it.type == KtRuleType.CLASS_DECLARATION }
            return classDeclarations.mapNotNull { getHeaderDeclaration(it) }
        }

        private fun getHeaderDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            return parseInterfDeclaration(classDeclaration)
                    ?: parseClassDeclaration(classDeclaration)
                    ?: parseEnumDeclaration(classDeclaration)
                    ?: parseStructDeclaration(classDeclaration)
        }

        private fun parseInterfDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return if ("_interf" in simpleIdentifiers) {
                val name = getDeclarationName(classDeclaration)
                if (name != null) {
                    if (classDeclaration.containsType(KtRuleType.CLASS_BODY)) {
                        val modportDeclarations = classDeclaration.childAs(KtRuleType.CLASS_BODY)
                                .childAs(KtRuleType.CLASS_MEMBER_DECLARATIONS)
                                .childrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                                .map { it.childAs(KtRuleType.DECLARATION) }
                                .map { it.firstAsRule() }
                                .filter { it.type == KtRuleType.CLASS_DECLARATION }
                        val modports = modportDeclarations.mapNotNull { parseModportDeclaration(it) }
                        HeaderDeclarationInterf(name, modports)
                    } else HeaderDeclarationInterf(name, listOf())
                } else null
            } else null
        }

        private fun parseModportDeclaration(classDeclaration: KtRule): String? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return if ("_modport" in simpleIdentifiers) {
                getDeclarationName(classDeclaration)
            } else null
        }

        private fun parseClassDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return if (classDeclaration.containsType(KtRuleType.CLASS_BODY)) {
                if ("_class" in simpleIdentifiers) {
                    getDeclarationName(classDeclaration)
                            .let { if (it != null) HeaderDeclarationClass(it, true) else null }
                } else {
                    if (simpleIdentifiers.none { it in listOf("_module", "_interf", "_class", "_enum", "_struct") }) {
                        getDeclarationName(classDeclaration)
                                .let { if (it != null) HeaderDeclarationClass(it, false) else null }
                    } else null
                }
            } else null
        }

        private fun parseEnumDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return if ("_enum" in simpleIdentifiers) {
                getDeclarationName(classDeclaration)
                        .let { if (it != null) HeaderDeclarationEnum(it) else null }
            } else null
        }

        private fun parseStructDeclaration(classDeclaration: KtRule): HeaderDeclaration? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return if ("_struct" in simpleIdentifiers) {
                getDeclarationName(classDeclaration)
                        .let { if (it != null) HeaderDeclarationStruct(it) else null }
            } else null
        }

        private fun getDeclarationName(classDeclaration: KtRule): String? {
            val underscoredName = classDeclaration.childAs(KtRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
            return if (underscoredName[0] == '_') {
                underscoredName.substring(1)
            } else null
        }

        private fun getDelegationSpecifierSimpleIdenfitiers(classDeclaration: KtRule): List<String> {
            return if (classDeclaration.containsType(KtRuleType.DELEGATION_SPECIFIERS)) {
                val delegationSpecifiers = classDeclaration.childAs(KtRuleType.DELEGATION_SPECIFIERS)
                        .childrenAs(KtRuleType.ANNOTATED_DELEGATION_SPECIFIER)
                        .map { it.childAs(KtRuleType.DELEGATION_SPECIFIER) }
                val userTypes = delegationSpecifiers
                        .map { it.firstAsRule() }
                        .filter { it.type == KtRuleType.USER_TYPE }
                val simpleUserTypes = userTypes
                        .filter { it.children.size == 1 }
                        .map { it.childAs(KtRuleType.SIMPLE_USER_TYPE) }
                simpleUserTypes
                        .filter { !it.containsType(KtRuleType.TYPE_ARGUMENTS) }
                        .map { it.childAs(KtRuleType.SIMPLE_IDENTIFIER) }
                        .map { it.firstAsTokenText() }
            } else listOf()
        }
    }
}
