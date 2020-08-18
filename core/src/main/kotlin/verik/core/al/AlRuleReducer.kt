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

package verik.core.al

import verik.core.main.LineException

object AlRuleReducer {

    fun reduce(rule: AlRule) {
        reduceRule(rule.type, rule)
        rule.children.forEach { if (it is AlRule) reduce(it) }
    }

    private fun reduceRule(ruleType: AlRuleType, rule: AlRule) {
        when (ruleType) {
            AlRuleType.PRIMARY_CONSTRUCTOR -> {
                if (rule.containsType(AlTokenType.CONSTRUCTOR)) {
                    throw LineException("\"constructor\" keyword is not permitted in primary constructor", rule)
                }
            }
            AlRuleType.DELEGATION_SPECIFIER -> {
                if (rule.containsType(AlRuleType.FUNCTION_TYPE)) {
                    throw LineException("class cannot extend function type", rule)
                }
            }
            AlRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.FUNCTION_DECLARATION -> {
                if (rule.containsType(AlRuleType.TYPE_PARAMETERS)) {
                    throw LineException("type parameters are not permitted here", rule)
                }
            }
            AlRuleType.VARIABLE_DECLARATION -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.PROPERTY_DECLARATION -> {
                if (rule.containsType(AlRuleType.TYPE_PARAMETERS)) {
                    throw LineException("type parameters are not permitted here", rule)
                }
            }
            AlRuleType.ENUM_ENTRY -> {
                if (rule.containsType(AlRuleType.MODIFIER)) {
                    throw LineException("modifiers on enum entries are not permitted", rule)
                }
                if (rule.containsType(AlRuleType.CLASS_BODY)) {
                    throw LineException("class bodies for enum entries are not permitted", rule)
                }
            }
            AlRuleType.TYPE_REFERENCE -> {
                if (rule.containsType(AlTokenType.DYNAMIC)) {
                    throw LineException("dynamic type references are not permitted", rule)
                }
            }
            AlRuleType.TYPE_PROJECTION -> {
                if (rule.containsType(AlTokenType.MULT)) {
                    throw LineException("star projected types are not permitted", rule)
                }
            }
            AlRuleType.STATEMENT -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.FOR_STATEMENT -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                if (rule.containsType(AlTokenType.LANGLE)) {
                    throw LineException("illegal expression", rule)
                }
            }
            AlRuleType.UNARY_PREFIX -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.POSTFIX_UNARY_EXPRESSION -> {
                if (rule.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                    throw LineException("type arguments are not permitted here", rule)
                }
            }
            AlRuleType.NAVIGATION_SUFFIX -> {
                if (rule.containsType(AlTokenType.CLASS)) {
                    throw LineException("illegal expression", rule)
                }
                if (rule.containsType(AlRuleType.PARENTHESIZED_EXPRESSION)) {
                    throw LineException("parenthesized expressions are not permitted here", rule)
                }
            }
            AlRuleType.CALL_SUFFIX -> {
                if (rule.containsType(AlRuleType.TYPE_ARGUMENTS)) {
                    throw LineException("type arguments are not permitted here", rule)
                }
            }
            AlRuleType.ANNOTATED_LAMBDA -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
            }
            AlRuleType.VALUE_ARGUMENT -> {
                if (rule.containsType(AlRuleType.ANNOTATION)) {
                    throw LineException("annotations are not permitted here", rule)
                }
                if (rule.containsType(AlRuleType.SIMPLE_IDENTIFIER)) {
                    throw LineException("named arguments are not supported", rule)
                }
                if (rule.containsType(AlTokenType.MULT)) {
                    throw LineException("spread operators are not supported", rule)
                }
            }
            AlRuleType.SUPER_EXPRESSION -> {
                if (rule.containsType(AlRuleType.TYPE)) {
                    throw LineException("typed super call is not supported", rule)
                }
                if (rule.containsType(AlRuleType.SIMPLE_IDENTIFIER)) {
                    throw LineException("labeled super call is not supported", rule)
                }
            }
            AlRuleType.WHEN_SUBJECT -> {
                if (rule.containsType(AlRuleType.VARIABLE_DECLARATION)) {
                    throw LineException("variable declaration for when subjects are not supported", rule)
                }
            }
            AlRuleType.CLASS_MODIFIER -> {
                if (rule.containsType(AlTokenType.SEALED)
                    || rule.containsType(AlTokenType.ANNOTATION)
                    || rule.containsType(AlTokenType.DATA)
                    || rule.containsType(AlTokenType.INNER)) {
                    throw LineException("class modifier is not supported", rule)
                }
            }
            AlRuleType.MEMBER_MODIFIER -> {
                if (rule.containsType(AlTokenType.LATEINIT)) {
                    throw LineException("member modifier is not supported", rule)
                }
            }
            AlRuleType.INHERITANCE_MODIFIER -> {
                if (rule.containsType(AlTokenType.ABSTRACT)) {
                    throw LineException("inheritance modifier is not supported", rule)
                }
            }
            else -> {}
        }
    }
}