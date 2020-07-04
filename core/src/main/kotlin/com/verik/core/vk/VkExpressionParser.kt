package com.verik.core.vk

import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

class VkExpressionParser {

    companion object {

        fun parse(expression: KtRule): VkExpression {
            val disjunction = expression.getChildAs(KtRuleType.DISJUNCTION, VkGrammarException())
            return parseDisjunction(disjunction)
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            var x = map(root.children[0].getAsRule(VkGrammarException()))
            for (child in root.children.drop(1)) {
                x = acc(x, map(child.getAsRule(VkGrammarException())))
            }
            return x
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            val iterator = root.children.iterator()
            var x = map(iterator.next().getAsRule(VkGrammarException()))
            while (iterator.hasNext()) {
                val op = iterator.next().getAsRule(VkGrammarException())
                val y = map(iterator.next().getAsRule(VkGrammarException()))
                x = acc(x, y, op)
            }
            return x
        }

        private fun reduceLeft(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0].getAsRule(VkGrammarException()))
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child.getAsRule(VkGrammarException()))
            }
            return x
        }

        private fun reduceRight(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            var x = map(root.children[0].getAsRule(VkGrammarException()))
            for (child in root.children.drop(1)) {
                x = acc(x, child.getAsRule(VkGrammarException()))
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
                val name = when (op.getFirstAsTokenType(VkGrammarException())) {
                    KtTokenType.EQEQ -> "eq"
                    KtTokenType.EXCL_EQ -> "neq"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(equality.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: KtRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val name = when (op.getFirstAsTokenType(VkGrammarException())) {
                    KtTokenType.LANGLE -> "lt"
                    KtTokenType.RANGLE -> "gt"
                    KtTokenType.LE -> "le"
                    KtTokenType.GE -> "ge"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(comparison.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: KtRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.getFirstAsRule(VkGrammarException())) }) { x, y, op ->
                val name = when (op.type) {
                    KtRuleType.IN_OPERATOR-> "in"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(infixOperation.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: KtRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val name = op.getFirstAsTokenText(VkGrammarException())
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
                } else throw VkParseException(infixFunctionCall.linePos, "infix operator $name not recognized")
            }
        }

        private fun parseRangeExpression(rangeExpression: KtRule): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkFunctionExpression(rangeExpression.linePos, "range_to", VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: KtRule): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val name = when (op.getFirstAsTokenType(VkGrammarException())) {
                    KtTokenType.ADD -> "add_tru"
                    KtTokenType.SUB -> "sub_tru"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(additiveExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: KtRule): VkExpression {
            val map = {it: KtRule -> parsePrefixUnaryExpression(it.getFirstAsRule(VkGrammarException()).getFirstAsRule(VkGrammarException())) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val name = when (op.getFirstAsTokenType(VkGrammarException())) {
                    KtTokenType.MULT -> "mul_tru"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(multiplicativeExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: KtRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.getFirstAsRule(VkGrammarException()).getFirst(VkGrammarException())
                val name = when {
                    operator is KtToken && operator.type == KtTokenType.ADD -> "unary_plus"
                    operator is KtToken && operator.type == KtTokenType.SUB -> "unary_minus"
                    operator is KtRule && operator.type == KtRuleType.EXCL -> "not"
                    else -> throw VkGrammarException()
                }
                VkFunctionExpression(prefixUnaryExpression.linePos, name, VkFunctionType.OPERATOR, listOf(x))
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: KtRule): VkExpression {
            return reduceRight(postfixUnaryExpression, { parsePrimaryExpression(it) }) { x, op ->
                val suffix = op.getFirstAsRule(VkGrammarException())
                when (suffix.type) {
                    KtRuleType.CALL_SUFFIX -> {
                        if (suffix.containsType(KtRuleType.ANNOTATED_LAMBDA)) {
                            throw VkParseException(suffix.linePos, "lambda expressions are not supported")
                        }
                        val valueArguments = suffix.getChildAs(KtRuleType.VALUE_ARGUMENTS, VkGrammarException()).getChildrenAs(KtRuleType.VALUE_ARGUMENT)
                        val expressions = valueArguments.map { VkExpression(it.getChildAs(KtRuleType.EXPRESSION, VkGrammarException())) }
                        VkFunctionExpression(postfixUnaryExpression.linePos, "invoke", VkFunctionType.OPERATOR, listOf(x) + expressions)
                    }
                    KtRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.getChildrenAs(KtRuleType.EXPRESSION).map { VkExpression(it) }
                        VkFunctionExpression(postfixUnaryExpression.linePos, "get", VkFunctionType.OPERATOR, listOf(x) + expressions)
                    }
                    KtRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
                        val identifier = simpleIdentifier.getFirstAsTokenText(VkGrammarException())
                        VkNavigationExpression(postfixUnaryExpression.linePos, x, identifier)
                    }
                    else -> throw VkGrammarException()
                }
            }
        }

        private fun parsePrimaryExpression(primaryExpression: KtRule): VkExpression {
            val child = primaryExpression.getFirstAsRule(VkGrammarException())
            return when(child.type) {
                KtRuleType.PARENTHESIZED_EXPRESSION -> {
                    VkExpression(child.getFirstAsRule(VkGrammarException()))
                }
                KtRuleType.SIMPLE_IDENTIFIER -> {
                    val identifier = child.getFirstAsTokenText(VkGrammarException())
                    VkIdentifierExpression(primaryExpression.linePos, identifier)
                }
                KtRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                KtRuleType.STRING_LITERAL -> {
                    parseStringLiteral(child)
                }
                KtRuleType.FUNCTION_LITERAL -> {
                    throw VkParseException(primaryExpression.linePos, "lambda expressions are not supported")
                }
                KtRuleType.THIS_EXPRESSION -> {
                    throw VkParseException(primaryExpression.linePos, "this expressions are not supported")
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    throw VkParseException(primaryExpression.linePos, "super expressions are not supported")
                }
                KtRuleType.IF_EXPRESSION -> {
                    throw VkParseException(primaryExpression.linePos, "if expressions are not supported")
                }
                KtRuleType.WHEN_EXPRESSION -> {
                    throw VkParseException(primaryExpression.linePos, "when expressions are not supported")
                }
                KtRuleType.JUMP_EXPRESSION -> {
                    throw VkParseException(primaryExpression.linePos, "jump expressions are not supported")
                }
                else -> throw VkGrammarException()
            }
        }

        private fun parseLiteralConstant(literalConstant: KtRule): VkExpression {
            val value = literalConstant.getFirstAsTokenText(VkGrammarException())
            return VkLiteralExpression(literalConstant.linePos, value)
        }

        private fun parseStringLiteral(literalConstant: KtRule): VkExpression {
            throw VkParseException(literalConstant.linePos, "string literals are not supported")
        }
    }
}