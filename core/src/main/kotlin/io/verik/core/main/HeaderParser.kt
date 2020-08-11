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

package io.verik.core.main

import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType

enum class HeaderDeclarationType {
    INTERF,
    MODPORT,
    CLASS,
    CLASS_CHILD,
    CLASS_COMPANION,
    ENUM,
    STRUCT
}

data class HeaderDeclaration(val identifier: String, val type: HeaderDeclarationType)

object HeaderParser {

    fun parse(kotlinFile: AlRule): List<HeaderDeclaration> {
        val classDeclarations = kotlinFile.childrenAs(AlRuleType.TOP_LEVEL_OBJECT)
                .map { it.childAs(AlRuleType.DECLARATION).firstAsRule() }
                .filter { it.type == AlRuleType.CLASS_DECLARATION }
        return classDeclarations.mapNotNull { getHeaderDeclaration(it) }
    }

    private fun getHeaderDeclaration(classDeclaration: AlRule): HeaderDeclaration? {
        val identifier = classDeclaration.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
        val simpleIdentifiers = getSimpleIdentifiers(classDeclaration)
        return when {
            "_interf" in simpleIdentifiers -> HeaderDeclaration(identifier, HeaderDeclarationType.INTERF)
            "_modport" in simpleIdentifiers -> HeaderDeclaration(identifier, HeaderDeclarationType.MODPORT)
            "_class" in simpleIdentifiers -> HeaderDeclaration(identifier, HeaderDeclarationType.CLASS)
            "_enum" in simpleIdentifiers -> HeaderDeclaration(identifier, HeaderDeclarationType.ENUM)
            "_struct" in simpleIdentifiers -> HeaderDeclaration(identifier, HeaderDeclarationType.STRUCT)
            else -> {
                if (classDeclaration.containsType(AlRuleType.CLASS_BODY) && "_module" !in simpleIdentifiers) {
                    if (identifier[0] == '_') {
                        HeaderDeclaration(identifier, HeaderDeclarationType.CLASS_CHILD)
                    } else {
                        HeaderDeclaration(identifier, HeaderDeclarationType.CLASS_COMPANION)
                    }
                } else null
            }
        }
    }

    private fun getSimpleIdentifiers(classDeclaration: AlRule): List<String> {
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
