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

package io.verik.core.al

import io.verik.core.FileLineException

class AlRuleReducer {

    companion object {

        fun reduce(rule: AlRule) {
            reduceRule(rule.type, rule)
            rule.children.forEach { if (it is AlRule) reduce(it) }
        }

        private fun reduceRule(ruleType: AlRuleType, rule: AlRule) {
            when (ruleType) {
                AlRuleType.CLASS_DECLARATION -> {
                    if (rule.containsType(AlTokenType.FUN)) {
                        throw FileLineException("functional interfaces are not permitted", rule.fileLine)
                    }
                }
                AlRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (rule.containsType(AlTokenType.CONSTRUCTOR)) {
                        throw FileLineException("\"constructor\" keyword is not permitted in primary constructor", rule.fileLine)
                    }
                }
                AlRuleType.DELEGATION_SPECIFIER -> {
                    if (rule.containsType(AlRuleType.FUNCTION_TYPE)) {
                        throw FileLineException("class cannot extend function type", rule.fileLine)
                    }
                }
                AlRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.FUNCTION_DECLARATION -> {
                    if (rule.containsType(AlRuleType.TYPE_PARAMETERS)) {
                        throw FileLineException("type parameters are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.VARIABLE_DECLARATION -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.PROPERTY_DECLARATION -> {
                    if (rule.containsType(AlRuleType.TYPE_PARAMETERS)) {
                        throw FileLineException("type parameters are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.ENUM_ENTRY -> {
                    if (rule.containsType(AlRuleType.MODIFIER)) {
                        throw FileLineException("modifiers on enum entries are not permitted", rule.fileLine)
                    }
                    if (rule.containsType(AlRuleType.CLASS_BODY)) {
                        throw FileLineException("class bodies for enum entries are not permitted", rule.fileLine)
                    }
                }
                AlRuleType.TYPE_REFERENCE -> {
                    if (rule.containsType(AlTokenType.DYNAMIC)) {
                        throw FileLineException("dynamic type references are not permitted", rule.fileLine)
                    }
                }
                AlRuleType.TYPE_PROJECTION -> {
                    if (rule.containsType(AlTokenType.MULT)) {
                        throw FileLineException("star projected types are not permitted", rule.fileLine)
                    }
                }
                AlRuleType.STATEMENT -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.FOR_STATEMENT -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (rule.containsType(AlTokenType.LANGLE)) {
                        throw FileLineException("illegal expression", rule.fileLine)
                    }
                }
                AlRuleType.UNARY_PREFIX -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.POSTFIX_UNARY_EXPRESSION -> {
                    if (rule.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                        throw FileLineException("type arguments are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.NAVIGATION_SUFFIX -> {
                    if (rule.containsType(AlTokenType.CLASS)) {
                        throw FileLineException("illegal expression", rule.fileLine)
                    }
                    if (rule.containsType(AlRuleType.PARENTHESIZED_EXPRESSION)) {
                        throw FileLineException("parenthesized expressions are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.CALL_SUFFIX -> {
                    if (rule.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                        throw FileLineException("type arguments are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.ANNOTATED_LAMBDA -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                AlRuleType.VALUE_ARGUMENT -> {
                    if (rule.containsType(AlRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                    if (rule.containsType(AlRuleType.SIMPLE_IDENTIFIER)) {
                        throw FileLineException("named arguments are not supported", rule.fileLine)
                    }
                    if (rule.containsType(AlTokenType.MULT)) {
                        throw FileLineException("spread operators are not supported", rule.fileLine)
                    }
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    if (rule.containsType(AlRuleType.TYPE)) {
                        throw FileLineException("typed super call is not supported", rule.fileLine)
                    }
                    if (rule.containsType(AlRuleType.SIMPLE_IDENTIFIER)) {
                        throw FileLineException("labeled super call is not supported", rule.fileLine)
                    }
                }
                AlRuleType.WHEN_SUBJECT -> {
                    if (rule.containsType(AlRuleType.VARIABLE_DECLARATION)) {
                        throw FileLineException("variable declaration for when subjects are not supported", rule.fileLine)
                    }
                }
                AlRuleType.CLASS_MODIFIER -> {
                    if (rule.containsType(AlTokenType.SEALED)
                        || rule.containsType(AlTokenType.ANNOTATION)
                        || rule.containsType(AlTokenType.DATA)
                        || rule.containsType(AlTokenType.INNER)) {
                        throw FileLineException("class modifier is not supported", rule.fileLine)
                    }
                }
                AlRuleType.MEMBER_MODIFIER -> {
                    if (rule.containsType(AlTokenType.LATEINIT)) {
                        throw FileLineException("member modifier is not supported", rule.fileLine)
                    }
                }
                AlRuleType.INHERITANCE_MODIFIER -> {
                    if (rule.containsType(AlTokenType.ABSTRACT)) {
                        throw FileLineException("inheritance modifier is not supported", rule.fileLine)
                    }
                    if (rule.containsType(AlTokenType.FINAL)) {
                        throw FileLineException("inheritance modifier is not supported", rule.fileLine)
                    }
                }
                else -> {}
            }
        }
    }
}