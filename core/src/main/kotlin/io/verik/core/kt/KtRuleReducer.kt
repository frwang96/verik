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

import io.verik.core.FileLineException

class KtRuleReducer {

    companion object {

        fun reduce(rule: KtRule) {
            reduceRule(rule.type, rule)
            rule.children.forEach { if (it is KtRule) reduce(it) }
        }

        private fun reduceRule(ruleType: KtRuleType, rule: KtRule) {
            when (ruleType) {
                KtRuleType.CLASS_DECLARATION -> {
                    if (rule.containsType(KtTokenType.FUN)) {
                        throw FileLineException("functional interfaces are not permitted", rule.fileLine)
                    }
                }
                KtRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (rule.containsType(KtTokenType.CONSTRUCTOR)) {
                        throw FileLineException("\"constructor\" keyword is not permitted in primary constructor", rule.fileLine)
                    }
                }
                KtRuleType.DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.FUNCTION_TYPE)) {
                        throw FileLineException("class cannot extend function type", rule.fileLine)
                    }
                }
                KtRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.FUNCTION_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw FileLineException("type parameters are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.VARIABLE_DECLARATION -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.PROPERTY_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw FileLineException("type parameters are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.ENUM_ENTRY -> {
                    if (rule.containsType(KtRuleType.MODIFIER)) {
                        throw FileLineException("modifiers on enum entries are not permitted", rule.fileLine)
                    }
                    if (rule.containsType(KtRuleType.CLASS_BODY)) {
                        throw FileLineException("class bodies for enum entries are not permitted", rule.fileLine)
                    }
                }
                KtRuleType.TYPE_REFERENCE -> {
                    if (rule.containsType(KtTokenType.DYNAMIC)) {
                        throw FileLineException("dynamic type references are not permitted", rule.fileLine)
                    }
                }
                KtRuleType.TYPE_PROJECTION -> {
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw FileLineException("star projected types are not permitted", rule.fileLine)
                    }
                }
                KtRuleType.STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.FOR_STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (rule.containsType(KtTokenType.LANGLE)) {
                        throw FileLineException("illegal expression", rule.fileLine)
                    }
                }
                KtRuleType.UNARY_PREFIX -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.POSTFIX_UNARY_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw FileLineException("type arguments are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.NAVIGATION_SUFFIX -> {
                    if (rule.containsType(KtTokenType.CLASS)) {
                        throw FileLineException("illegal expression", rule.fileLine)
                    }
                    if (rule.containsType(KtRuleType.PARENTHESIZED_EXPRESSION)) {
                        throw FileLineException("parenthesized expressions are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.CALL_SUFFIX -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw FileLineException("type arguments are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.ANNOTATED_LAMBDA -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                }
                KtRuleType.VALUE_ARGUMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw FileLineException("annotations are not permitted here", rule.fileLine)
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw FileLineException("named arguments are not supported", rule.fileLine)
                    }
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw FileLineException("spread operators are not supported", rule.fileLine)
                    }
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE)) {
                        throw FileLineException("typed super call is not supported", rule.fileLine)
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw FileLineException("labeled super call is not supported", rule.fileLine)
                    }
                }
                KtRuleType.WHEN_SUBJECT -> {
                    if (rule.containsType(KtRuleType.VARIABLE_DECLARATION)) {
                        throw FileLineException("variable declaration for when subjects are not supported", rule.fileLine)
                    }
                }
                KtRuleType.CLASS_MODIFIER -> {
                    if (rule.containsType(KtTokenType.SEALED)
                        || rule.containsType(KtTokenType.ANNOTATION)
                        || rule.containsType(KtTokenType.DATA)
                        || rule.containsType(KtTokenType.INNER)) {
                        throw FileLineException("class modifier is not supported", rule.fileLine)
                    }
                }
                KtRuleType.MEMBER_MODIFIER -> {
                    if (rule.containsType(KtTokenType.LATEINIT)) {
                        throw FileLineException("member modifier is not supported", rule.fileLine)
                    }
                }
                KtRuleType.INHERITANCE_MODIFIER -> {
                    if (rule.containsType(KtTokenType.ABSTRACT)) {
                        throw FileLineException("inheritance modifier is not supported", rule.fileLine)
                    }
                    if (rule.containsType(KtTokenType.FINAL)) {
                        throw FileLineException("inheritance modifier is not supported", rule.fileLine)
                    }
                }
                else -> {}
            }
        }
    }
}