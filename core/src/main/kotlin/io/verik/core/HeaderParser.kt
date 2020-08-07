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

import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

enum class HeaderDeclarationType {
    INTERF,
    MODPORT,
    CLASS,
    SUBCLASS,
    ENUM,
    STRUCT
}

data class HeaderDeclaration(val name: String, val type: HeaderDeclarationType)

class HeaderParser {

    companion object {

        fun parse(kotlinFile: AlRule): List<HeaderDeclaration> {
            val classDeclarations = kotlinFile.childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
                    .map { it.childAs(AlRuleType.DECLARATION).firstAsRule() }
                    .filter { it.type == AlRuleType.CLASS_DECLARATION }
            return classDeclarations.mapNotNull { getHeaderDeclaration(it) }
        }

        private fun getHeaderDeclaration(classDeclaration: AlRule): HeaderDeclaration? {
            val simpleIdentifiers = getDelegationSpecifierSimpleIdenfitiers(classDeclaration)
            return when {
                "_interf" in simpleIdentifiers -> getHeaderDeclaration(classDeclaration, HeaderDeclarationType.INTERF)
                "_modport" in simpleIdentifiers -> getHeaderDeclaration(classDeclaration, HeaderDeclarationType.MODPORT)
                "_class" in simpleIdentifiers -> getHeaderDeclaration(classDeclaration, HeaderDeclarationType.CLASS)
                "_enum" in simpleIdentifiers -> getHeaderDeclaration(classDeclaration, HeaderDeclarationType.ENUM)
                "_struct" in simpleIdentifiers -> getHeaderDeclaration(classDeclaration, HeaderDeclarationType.STRUCT)
                else -> {
                    if (classDeclaration.containsType(AlRuleType.CLASS_BODY) && "_module" !in simpleIdentifiers) {
                        getHeaderDeclaration(classDeclaration, HeaderDeclarationType.SUBCLASS)
                    } else null
                }
            }
        }

        private fun getHeaderDeclaration(classDeclaration: AlRule, type: HeaderDeclarationType): HeaderDeclaration? {
            val underscoredName = classDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
            return if (underscoredName[0] == '_') {
                HeaderDeclaration(underscoredName.substring(1), type)
            } else null
        }

        private fun getDelegationSpecifierSimpleIdenfitiers(classDeclaration: AlRule): List<String> {
            return if (classDeclaration.containsType(AlRuleType.DELEGATION_SPECIFIERS)) {
                val delegationSpecifiers = classDeclaration.childAs(AlRuleType.DELEGATION_SPECIFIERS)
                        .childrenAs(AlRuleType.ANNOTATED_DELEGATION_SPECIFIER)
                        .map { it.childAs(AlRuleType.DELEGATION_SPECIFIER) }
                val userTypes = delegationSpecifiers
                        .map { it.firstAsRule() }
                        .filter { it.type == AlRuleType.USER_TYPE }
                val simpleUserTypes = userTypes
                        .filter { it.children.size == 1 }
                        .map { it.childAs(AlRuleType.SIMPLE_USER_TYPE) }
                simpleUserTypes
                        .filter { !it.containsType(AlRuleType.TYPE_ARGUMENTS) }
                        .map { it.childAs(AlRuleType.SIMPLE_IDENTIFIER) }
                        .map { it.firstAsTokenText() }
            } else listOf()
        }
    }
}
