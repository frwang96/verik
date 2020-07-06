package com.verik.core.vk

import com.verik.core.kt.*

// Copyright (c) 2020 Francis Wang

class VkExpressionParser {

    companion object {

        fun parse(expression: KtRule): VkExpression {
            val disjunction = expression.getChildAs(KtRuleType.DISJUNCTION)
            return parseDisjunction(disjunction)
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw KtGrammarException("rule node has no children", root.linePos)
            var x = map(root.children[0].getAsRule())
            for (child in root.children.drop(1)) {
                x = acc(x, map(child.getAsRule()))
            }
            return x
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw KtGrammarException("rule node has no children", root.linePos)
            val iterator = root.children.iterator()
            var x = map(iterator.next().getAsRule())
            while (iterator.hasNext()) {
                val op = iterator.next().getAsRule()
                val y = map(iterator.next().getAsRule())
                x = acc(x, y, op)
            }
            return x
        }

        private fun reduceLeft(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw KtGrammarException("rule node has no children", root.linePos)
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0].getAsRule())
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child.getAsRule())
            }
            return x
        }

        private fun reduceRight(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw KtGrammarException("rule node has no children", root.linePos)
            var x = map(root.children[0].getAsRule())
            for (child in root.children.drop(1)) {
                x = acc(x, child.getAsRule())
            }
            return x
        }

        private fun parseDisjunction(disjunction: KtRule): VkExpression {
            return reduce(disjunction, { parseConjunction(it) }) { x, y ->
                VkFunctionExpression(disjunction.linePos, "or", VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseConjunction(conjunction: KtRule): VkExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                VkFunctionExpression(conjunction.linePos, "and", VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseEquality(equality: KtRule): VkExpression {
            return reduce(equality, { parseComparison(it) }) { x, y, op ->
                val name = when (op.getFirstAsTokenType()) {
                    KtTokenType.EQEQ -> "eq"
                    KtTokenType.EXCL_EQ -> "neq"
                    else -> throw KtGrammarException("equality operator expected", equality.linePos)
                }
                VkFunctionExpression(equality.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: KtRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val name = when (op.getFirstAsTokenType()) {
                    KtTokenType.LANGLE -> "lt"
                    KtTokenType.RANGLE -> "gt"
                    KtTokenType.LE -> "le"
                    KtTokenType.GE -> "ge"
                    else -> throw KtGrammarException("comparison operator expected", comparison.linePos)
                }
                VkFunctionExpression(comparison.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: KtRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.getFirstAsRule()) }) { x, y, op ->
                val name = when (op.type) {
                    KtRuleType.IN_OPERATOR-> "in"
                    else -> throw KtGrammarException("in operator expected", infixOperation.linePos)
                }
                VkFunctionExpression(infixOperation.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: KtRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val name = op.getFirstAsTokenText()
                val infixOperators = listOf(
                        "with", "con", "put", "reg", "drive", "for_each", "until",
                        "eq", "neq", "add", "sub", "mul", "sl", "sr", "rotl", "rotr",
                        "sl_ext", "sr_tru", "and", "or", "xor", "nand", "nor", "xnor",
                        "cat", "put_add", "reg_add", "put_sub", "reg_sub", "put_mul", "reg_mul",
                        "put_sl", "reg_sl", "put_sr", "reg_sr", "put_rotl", "reg_rotl",
                        "put_and", "reg_and", "put_or", "reg_or", "put_xor", "reg_xor",
                        "put_nand", "reg_nand", "put_nor", "reg_nor", "put_xnor", "reg_xnor"
                )
                if (name in infixOperators) {
                    VkFunctionExpression(infixFunctionCall.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
                } else throw VkParseException("infix operator $name not recognized", infixFunctionCall.linePos)
            }
        }

        private fun parseRangeExpression(rangeExpression: KtRule): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkFunctionExpression(rangeExpression.linePos, "range_to", VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: KtRule): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val name = when (op.getFirstAsTokenType()) {
                    KtTokenType.ADD -> "add_tru"
                    KtTokenType.SUB -> "sub_tru"
                    else -> throw VkParseException("additive operator expected", additiveExpression.linePos)
                }
                VkFunctionExpression(additiveExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: KtRule): VkExpression {
            val map = {it: KtRule -> parsePrefixUnaryExpression(it.getFirstAsRule().getFirstAsRule()) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val name = when (op.getFirstAsTokenType()) {
                    KtTokenType.MULT -> "mul_tru"
                    else -> throw VkParseException("multiplicative operator expected", multiplicativeExpression.linePos)
                }
                VkFunctionExpression(multiplicativeExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: KtRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.getFirstAsRule().getFirst()
                val name = when {
                    operator is KtToken && operator.type == KtTokenType.ADD -> "unary_plus"
                    operator is KtToken && operator.type == KtTokenType.SUB -> "unary_minus"
                    operator is KtRule && operator.type == KtRuleType.EXCL -> "not"
                    else -> throw VkParseException("prefix unary operator expected", prefixUnaryExpression.linePos)
                }
                VkFunctionExpression(prefixUnaryExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x))
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: KtRule): VkExpression {
            return reduceRight(postfixUnaryExpression, { parsePrimaryExpression(it) }) { x, op ->
                val suffix = op.getFirstAsRule()
                when (suffix.type) {
                    KtRuleType.CALL_SUFFIX -> {
                        if (suffix.containsType(KtRuleType.ANNOTATED_LAMBDA)) {
                            throw VkParseException("lambda expressions are not supported", suffix.linePos)
                        }
                        val valueArguments = suffix.getChildAs(KtRuleType.VALUE_ARGUMENTS).getChildrenAs(KtRuleType.VALUE_ARGUMENT)
                        val expressions = valueArguments.map { VkExpression(it.getChildAs(KtRuleType.EXPRESSION)) }
                        VkFunctionExpression(postfixUnaryExpression.linePos, "invoke", VkFunctionType.OPERATOR, listOf(x) + expressions)
                    }
                    KtRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.getChildrenAs(KtRuleType.EXPRESSION).map { VkExpression(it) }
                        VkFunctionExpression(postfixUnaryExpression.linePos, "get", VkFunctionType.OPERATOR, listOf(x) + expressions)
                    }
                    KtRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.getChildAs(KtRuleType.SIMPLE_IDENTIFIER)
                        val identifier = simpleIdentifier.getFirstAsTokenText()
                        VkNavigationExpression(postfixUnaryExpression.linePos, x, identifier)
                    }
                    else -> throw VkParseException("postfix unary suffix expected", postfixUnaryExpression.linePos)
                }
            }
        }

        private fun parsePrimaryExpression(primaryExpression: KtRule): VkExpression {
            val child = primaryExpression.getFirstAsRule()
            return when(child.type) {
                KtRuleType.PARENTHESIZED_EXPRESSION -> {
                    VkExpression(child.getFirstAsRule())
                }
                KtRuleType.SIMPLE_IDENTIFIER -> {
                    val identifier = child.getFirstAsTokenText()
                    VkIdentifierExpression(primaryExpression.linePos, identifier)
                }
                KtRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                KtRuleType.STRING_LITERAL -> {
                    parseStringLiteral(child)
                }
                KtRuleType.FUNCTION_LITERAL -> {
                    throw VkParseException("lambda expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.THIS_EXPRESSION -> {
                    throw VkParseException("this expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    throw VkParseException("super expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.IF_EXPRESSION -> {
                    throw VkParseException("if expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.WHEN_EXPRESSION -> {
                    throw VkParseException("when expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.JUMP_EXPRESSION -> {
                    throw VkParseException("jump expressions are not supported", primaryExpression.linePos)
                }
                else -> throw KtGrammarException("primary expression expected", primaryExpression.linePos)
            }
        }

        private fun parseLiteralConstant(literalConstant: KtRule): VkExpression {
            val value = literalConstant.getFirstAsTokenText()
            return VkLiteralExpression(literalConstant.linePos, value)
        }

        private fun parseStringLiteral(literalConstant: KtRule): VkExpression {
            throw VkParseException("string literals are not supported", literalConstant.linePos)
        }
    }
}