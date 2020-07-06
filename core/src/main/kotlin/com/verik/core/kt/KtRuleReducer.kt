package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

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
                        throw KtParseException("functional interfaces are not permitted", rule.linePos)
                    }
                }
                KtRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (rule.containsType(KtTokenType.CONSTRUCTOR)) {
                        throw KtParseException("\"constructor\" keyword is not permitted in primary constructor", rule.linePos)
                    }
                }
                KtRuleType.DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.FUNCTION_TYPE)) {
                        throw KtParseException("class cannot extend function type", rule.linePos)
                    }
                }
                KtRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.FUNCTION_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException("type parameters are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.VARIABLE_DECLARATION -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.PROPERTY_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException("type parameters are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.ENUM_ENTRY -> {
                    if (rule.containsType(KtRuleType.MODIFIER)) {
                        throw KtParseException("modifiers on enum entries are not permitted", rule.linePos)
                    }
                    if (rule.containsType(KtRuleType.CLASS_BODY)) {
                        throw KtParseException("class bodies for enum entries are not permitted", rule.linePos)
                    }
                }
                KtRuleType.TYPE_REFERENCE -> {
                    if (rule.containsType(KtTokenType.DYNAMIC)) {
                        throw KtParseException("dynamic type references are not permitted", rule.linePos)
                    }
                }
                KtRuleType.TYPE_PROJECTION -> {
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw KtParseException("star projected types are not permitted", rule.linePos)
                    }
                }
                KtRuleType.STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.FOR_STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (rule.containsType(KtTokenType.LANGLE)) {
                        throw KtParseException("illegal expression", rule.linePos)
                    }
                }
                KtRuleType.UNARY_PREFIX -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.POSTFIX_UNARY_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException("type arguments are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.NAVIGATION_SUFFIX -> {
                    if (rule.containsType(KtTokenType.CLASS)) {
                        throw KtParseException("illegal expression", rule.linePos)
                    }
                    if (rule.containsType(KtRuleType.PARENTHESIZED_EXPRESSION)) {
                        throw KtParseException("parenthesized expressions are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.CALL_SUFFIX -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException("type arguments are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.ANNOTATED_LAMBDA -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                }
                KtRuleType.VALUE_ARGUMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException("annotations are not permitted here", rule.linePos)
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException("named arguments are not supported", rule.linePos)
                    }
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw KtParseException("spread operators are not supported", rule.linePos)
                    }
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE)) {
                        throw KtParseException("typed super call is not supported", rule.linePos)
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException("labeled super call is not supported", rule.linePos)
                    }
                }
                KtRuleType.WHEN_SUBJECT -> {
                    if (rule.containsType(KtRuleType.VARIABLE_DECLARATION)) {
                        throw KtParseException("variable declaration for when subjects are not supported", rule.linePos)
                    }
                }
                KtRuleType.CLASS_MODIFIER -> {
                    if (rule.containsType(KtTokenType.SEALED)
                        || rule.containsType(KtTokenType.ANNOTATION)
                        || rule.containsType(KtTokenType.DATA)
                        || rule.containsType(KtTokenType.INNER)) {
                        throw KtParseException("class modifier is not supported", rule.linePos)
                    }
                }
                KtRuleType.MEMBER_MODIFIER -> {
                    if (rule.containsType(KtTokenType.LATEINIT)) {
                        throw KtParseException("member modifier is not supported", rule.linePos)
                    }
                }
                KtRuleType.INHERITANCE_MODIFIER -> {
                    if (rule.containsType(KtTokenType.ABSTRACT)) {
                        throw KtParseException("inheritance modifier is not supported", rule.linePos)
                    }
                    if (rule.containsType(KtTokenType.FINAL)) {
                        throw KtParseException("inheritance modifier is not supported", rule.linePos)
                    }
                }
                else -> {}
            }
        }
    }
}