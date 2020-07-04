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
                        throw KtParseException(rule.linePos, "functional interfaces are not permitted")
                    }
                }
                KtRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (rule.containsType(KtTokenType.CONSTRUCTOR)) {
                        throw KtParseException(rule.linePos, "\"constructor\" keyword is not permitted in primary constructor")
                    }
                }
                KtRuleType.DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.FUNCTION_TYPE)) {
                        throw KtParseException(rule.linePos, "class cannot extend function type")
                    }
                }
                KtRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.FUNCTION_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException(rule.linePos, "type parameters are not permitted here")
                    }
                }
                KtRuleType.VARIABLE_DECLARATION -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.PROPERTY_DECLARATION -> {
                    if (rule.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException(rule.linePos, "type parameters are not permitted here")
                    }
                }
                KtRuleType.ENUM_ENTRY -> {
                    if (rule.containsType(KtRuleType.MODIFIER)) {
                        throw KtParseException(rule.linePos, "modifiers on enum entries are not permitted")
                    }
                    if (rule.containsType(KtRuleType.CLASS_BODY)) {
                        throw KtParseException(rule.linePos, "class bodies for enum entries are not permitted")
                    }
                }
                KtRuleType.TYPE_REFERENCE -> {
                    if (rule.containsType(KtTokenType.DYNAMIC)) {
                        throw KtParseException(rule.linePos, "dynamic type references are not permitted")
                    }
                }
                KtRuleType.TYPE_PROJECTION -> {
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw KtParseException(rule.linePos, "star projected types are not permitted")
                    }
                }
                KtRuleType.STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.FOR_STATEMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (rule.containsType(KtTokenType.LANGLE)) {
                        throw KtParseException(rule.linePos, "illegal expression")
                    }
                }
                KtRuleType.UNARY_PREFIX -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.POSTFIX_UNARY_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException(rule.linePos, "type arguments are not permitted here")
                    }
                }
                KtRuleType.NAVIGATION_SUFFIX -> {
                    if (rule.containsType(KtTokenType.CLASS)) {
                        throw KtParseException(rule.linePos, "illegal expression")
                    }
                    if (rule.containsType(KtRuleType.PARENTHESIZED_EXPRESSION)) {
                        throw KtParseException(rule.linePos, "parenthesized expressions are not permitted here")
                    }
                }
                KtRuleType.CALL_SUFFIX -> {
                    if (rule.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException(rule.linePos, "type arguments are not permitted here")
                    }
                }
                KtRuleType.ANNOTATED_LAMBDA -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.VALUE_ARGUMENT -> {
                    if (rule.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(rule.linePos, "annotations are not permitted here")
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(rule.linePos, "named arguments are not supported")
                    }
                    if (rule.containsType(KtTokenType.MULT)) {
                        throw KtParseException(rule.linePos, "spread operators are not supported")
                    }
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    if (rule.containsType(KtRuleType.TYPE)) {
                        throw KtParseException(rule.linePos, "typed super call is not supported")
                    }
                    if (rule.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(rule.linePos, "labeled super call is not supported")
                    }
                }
                KtRuleType.WHEN_SUBJECT -> {
                    if (rule.containsType(KtRuleType.VARIABLE_DECLARATION)) {
                        throw KtParseException(rule.linePos, "variable declaration for when subjects are not supported")
                    }
                }
                KtRuleType.CLASS_MODIFIER -> {
                    if (rule.containsType(KtTokenType.SEALED)
                        || rule.containsType(KtTokenType.ANNOTATION)
                        || rule.containsType(KtTokenType.DATA)
                        || rule.containsType(KtTokenType.INNER)) {
                        throw KtParseException(rule.linePos, "class modifier is not supported")
                    }
                }
                KtRuleType.MEMBER_MODIFIER -> {
                    if (rule.containsType(KtTokenType.LATEINIT)) {
                        throw KtParseException(rule.linePos, "member modifier is not supported")
                    }
                }
                KtRuleType.INHERITANCE_MODIFIER -> {
                    if (rule.containsType(KtTokenType.FINAL)) {
                        throw KtParseException(rule.linePos, "inheritance modifier is not supported")
                    }
                }
                else -> {}
            }
        }
    }
}