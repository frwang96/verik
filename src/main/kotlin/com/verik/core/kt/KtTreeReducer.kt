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
                    if (tree.childrenContains(KtTokenType.FUN)) {
                        throw KtParseException(tree.linePos, "functional interfaces are not permitted")
                    }
                }
                KtRuleType.PRIMARY_CONSTRUCTOR -> {
                    if (tree.childrenContains(KtTokenType.CONSTRUCTOR)) {
                        throw KtParseException(tree.linePos, "\"constructor\" keyword is not permitted in primary constructor")
                    }
                }
                KtRuleType.DELEGATION_SPECIFIER -> {
                    if (tree.childrenContains(KtRuleType.FUNCTION_TYPE)) {
                        throw KtParseException(tree.linePos, "class cannot extend function type")
                    }
                }
                KtRuleType.ANNOTATED_DELEGATION_SPECIFIER -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.VARIABLE_DECLARATION -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.ENUM_ENTRY -> {
                    if (tree.childrenContains(KtRuleType.MODIFIER)) {
                        throw KtParseException(tree.linePos, "modifiers on enum entries are not permitted")
                    }
                    if (tree.childrenContains(KtRuleType.CLASS_BODY)) {
                        throw KtParseException(tree.linePos, "class bodies for enum entries are not permitted")
                    }
                }
                KtRuleType.TYPE_REFERENCE -> {
                    if (tree.childrenContains(KtTokenType.DYNAMIC)) {
                        throw KtParseException(tree.linePos, "dynamic type references are not permitted")
                    }
                }
                KtRuleType.TYPE_PROJECTION -> {
                    if (tree.childrenContains(KtTokenType.MULT)) {
                        throw KtParseException(tree.linePos, "star projected types are not permitted")
                    }
                }
                KtRuleType.STATEMENT -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.FOR_STATEMENT -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.COMPARISON_WITH_LITERAL_RIGHT_SIDE -> {
                    if (tree.childrenContains(KtTokenType.LANGLE)) {
                        throw KtParseException(tree.linePos, "illegal expression")
                    }
                }
                KtRuleType.UNARY_PREFIX -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.NAVIGATION_SUFFIX -> {
                    if (tree.childrenContains(KtTokenType.CLASS)) {
                        throw KtParseException(tree.linePos, "illegal expression")
                    }
                }
                KtRuleType.ANNOTATED_LAMBDA -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                }
                KtRuleType.VALUE_ARGUMENT -> {
                    if (tree.childrenContains(KtRuleType.ANNOTATION)) {
                        throw KtParseException(tree.linePos, "annotations are not permitted here")
                    }
                    if (tree.childrenContains(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(tree.linePos, "named arguments are not supported")
                    }
                    if (tree.childrenContains(KtTokenType.MULT)) {
                        throw KtParseException(tree.linePos, "spread operators are not supported")
                    }
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    if (tree.childrenContains(KtRuleType.TYPE)) {
                        throw KtParseException(tree.linePos, "typed super call is not supported")
                    }
                    if (tree.childrenContains(KtRuleType.SIMPLE_IDENTIFIER)) {
                        throw KtParseException(tree.linePos, "labeled super call is not supported")
                    }
                }
                KtRuleType.WHEN_SUBJECT -> {
                    if (tree.childrenContains(KtRuleType.VARIABLE_DECLARATION)) {
                        throw KtParseException(tree.linePos, "variable declaration for when subjects are not supported")
                    }
                }
                KtRuleType.CLASS_MODIFIER -> {
                    if (tree.childrenContains(KtTokenType.SEALED)
                        || tree.childrenContains(KtTokenType.ANNOTATION)
                        || tree.childrenContains(KtTokenType.DATA)
                        || tree.childrenContains(KtTokenType.INNER)) {
                        throw KtParseException(tree.linePos, "class modifier is not supported")
                    }
                }
                KtRuleType.MEMBER_MODIFIER -> {
                    if (tree.childrenContains(KtTokenType.LATEINIT)) {
                        throw KtParseException(tree.linePos, "member modifier is not supported")
                    }
                }
                KtRuleType.INHERITANCE_MODIFIER -> {
                    if (tree.childrenContains(KtTokenType.FINAL)) {
                        throw KtParseException(tree.linePos, "inheritance modifier is not supported")
                    }
                }
                else -> {}
            }
        }
    }
}