package com.verik.core.kt

// Copyright (c) 2020 Francis Wang

class KtTreeReducer {

    companion object {

        fun reduce(tree: KtTree) {
            if (tree.node is KtRule) {
                reduceRule(tree.node.type, tree)
                tree.children.forEach { reduce(it) }
            }
        }

        private fun reduceRule(rule: KtRuleType, tree: KtTree) {
            when (rule) {
                KtRuleType.CLASS_DECLARATION -> {
                    if (tree.containsType(KtTokenType.FUN)) {
                        throw KtParseException(tree.linePos, "functional interfaces are not permitted")
                    }
                }
                KtRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (tree.containsType(KtTokenType.CONSTRUCTOR)) {
                        throw KtParseException(tree.linePos, "\"constructor\" keyword is not permitted in primary constructor")
                    }
                }
                KtRuleType.DELEGATION_SPECIFIER -> {
                    if (tree.containsType(KtRuleType.FUNCTION_TYPE)) {
                        throw KtParseException(tree.linePos, "class cannot extend function type")
                    }
                }
                KtRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.FUNCTION_DECLARATION -> {
                    if (tree.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException(tree.linePos, "type parameters are not permitted here")
                    }
                }
                KtRuleType.VARIABLE_DECLARATION -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.PROPERTY_DECLARATION -> {
                    if (tree.containsType(KtRuleType.TYPE_PARAMETERS)) {
                        throw KtParseException(tree.linePos, "type parameters are not permitted here")
                    }
                }
                KtRuleType.ENUM_ENTRY -> {
                    if (tree.containsType(KtRuleType.MODIFIER)) {
                        throw KtParseException(tree.linePos, "modifiers on enum entries are not permitted")
                    }
                    if (tree.containsType(KtRuleType.CLASS_BODY)) {
                        throw KtParseException(tree.linePos, "class bodies for enum entries are not permitted")
                    }
                }
                KtRuleType.TYPE_REFERENCE -> {
                    if (tree.containsType(KtTokenType.DYNAMIC)) {
                        throw KtParseException(tree.linePos, "dynamic type references are not permitted")
                    }
                }
                KtRuleType.TYPE_PROJECTION -> {
                    if (tree.containsType(KtTokenType.MULT)) {
                        throw KtParseException(tree.linePos, "star projected types are not permitted")
                    }
                }
                KtRuleType.STATEMENT -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.FOR_STATEMENT -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (tree.containsType(KtTokenType.LANGLE)) {
                        throw KtParseException(tree.linePos, "illegal expression")
                    }
                }
                KtRuleType.UNARY_PREFIX -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.POSTFIX_UNARY_EXPRESSION -> {
                    if (tree.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException(tree.linePos, "type arguments are not permitted here")
                    }
                }
                KtRuleType.NAVIGATION_SUFFIX -> {
                    if (tree.containsType(KtTokenType.CLASS)) {
                        throw KtParseException(tree.linePos, "illegal expression")
                    }
                    if (tree.containsType(KtRuleType.PARENTHESIZED_EXPRESSION)) {
                        throw KtParseException(tree.linePos, "parenthesized expressions are not permitted here")
                    }
                }
                KtRuleType.CALL_SUFFIX -> {
                    if (tree.containsType(KtRuleType.TYPE_ARGUMENTS)) {
                        throw KtParseException(tree.linePos, "type arguments are not permitted here")
                    }
                }
                KtRuleType.ANNOTATED_LAMBDA -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.VALUE_ARGUMENT -> {
                    if (tree.containsType(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                    if (tree.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(tree.linePos, "named arguments are not supported")
                    }
                    if (tree.containsType(KtTokenType.MULT)) {
                        throw KtParseException(tree.linePos, "spread operators are not supported")
                    }
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    if (tree.containsType(KtRuleType.TYPE)) {
                        throw KtParseException(tree.linePos, "typed super call is not supported")
                    }
                    if (tree.containsType(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(tree.linePos, "labeled super call is not supported")
                    }
                }
                KtRuleType.WHEN_SUBJECT -> {
                    if (tree.containsType(KtRuleType.VARIABLE_DECLARATION)) {
                        throw KtParseException(tree.linePos, "variable declaration for when subjects are not supported")
                    }
                }
                KtRuleType.CLASS_MODIFIER -> {
                    if (tree.containsType(KtTokenType.SEALED)
                        || tree.containsType(KtTokenType.ANNOTATION)
                        || tree.containsType(KtTokenType.DATA)
                        || tree.containsType(KtTokenType.INNER)) {
                        throw KtParseException(tree.linePos, "class modifier is not supported")
                    }
                }
                KtRuleType.MEMBER_MODIFIER -> {
                    if (tree.containsType(KtTokenType.LATEINIT)) {
                        throw KtParseException(tree.linePos, "member modifier is not supported")
                    }
                }
                KtRuleType.INHERITANCE_MODIFIER -> {
                    if (tree.containsType(KtTokenType.FINAL)) {
                        throw KtParseException(tree.linePos, "inheritance modifier is not supported")
                    }
                }
                else -> {}
            }
        }
    }
}