package com.verik.core.vk

import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType
import com.verik.core.kt.KtTree

// Copyright (c) 2020 Francis Wang

data class VkExpression(var dataType: VkDataType, val body: VkExpressionBody) {

    companion object {

        operator fun invoke(body: VkExpressionBody): VkExpression {
            return VkExpression(VkUnitType, body)
        }

        operator fun invoke(expression: KtTree): VkExpression {
            val disjunction = expression.getChildAs(KtRuleType.DISJUNCTION, VkGrammarException())
            return parseDisjunction(disjunction)
        }

        fun reduce(root: KtTree, map: (KtTree) -> VkExpression, acc: (VkExpression, VkExpression) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            var x = map(root.children[0])
            for (child in root.children.drop(1)) {
                x = acc(x, map(child))
            }
            return x
        }

        fun reduce(root: KtTree, map: (KtTree) -> VkExpression, acc: (VkExpression, VkExpression, KtTree) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            val iterator = root.children.iterator()
            var x = map(iterator.next())
            while (iterator.hasNext()) {
                val op = iterator.next()
                val y = map(iterator.next())
                x = acc(x, y, op)
            }
            return x
        }

        fun reduceLeft(root: KtTree, map: (KtTree) -> VkExpression, acc: (VkExpression, KtTree) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0])
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child)
            }
            return x
        }

        fun reduceRight(root: KtTree, map: (KtTree) -> VkExpression, acc: (VkExpression, KtTree) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw VkGrammarException()
            var x = map(root.children[0])
            for (child in root.children.drop(1)) {
                x = acc(x, child)
            }
            return x
        }

        private fun parseDisjunction(disjunction: KtTree): VkExpression {
            return reduce(disjunction, { parseConjunction(it) }) { x, y ->
                VkExpression(VkFunctionExpression("or", VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseConjunction(conjunction: KtTree): VkExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                VkExpression(VkFunctionExpression("and", VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseEquality(equality: KtTree): VkExpression {
            return reduce(equality, { parseComparison(it) }) { x, y, op ->
                val name = when (op.first().getTypeAsToken(VkGrammarException())) {
                    KtTokenType.EQEQ -> "eq"
                    KtTokenType.EXCL_EQ -> "neq"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseComparison(comparison: KtTree): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val name = when (op.first().getTypeAsToken(VkGrammarException())) {
                    KtTokenType.LANGLE -> "lt"
                    KtTokenType.RANGLE -> "gt"
                    KtTokenType.LE -> "le"
                    KtTokenType.GE -> "ge"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseInfixOperation(infixOperation: KtTree): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.first()) }) { x, y, op ->
                val name = when (op.getTypeAsRule(VkGrammarException())) {
                    KtRuleType.IN_OPERATOR-> "in"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: KtTree): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val name = (op.first().node as KtToken).text
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
                    VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
                } else throw VkParseException(infixFunctionCall.linePos, "infix operator $name not recognized")
            }
        }

        private fun parseRangeExpression(rangeExpression: KtTree): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkExpression(VkFunctionExpression("range_to", VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: KtTree): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val name = when (op.first().getTypeAsToken(VkGrammarException())) {
                    KtTokenType.ADD -> "add_tru"
                    KtTokenType.SUB -> "sub_tru"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: KtTree): VkExpression {
            return reduce(multiplicativeExpression, { parsePrefixUnaryExpression(it.first().first()) }) { x, y, op ->
                val name = when (op.first().getTypeAsToken(VkGrammarException())) {
                    KtTokenType.MULT -> "mul_tru"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x, y)))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: KtTree): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.first().first()
                val name = when {
                    operator.isType(KtTokenType.ADD) -> "unary_plus"
                    operator.isType(KtTokenType.SUB) -> "unary_minus"
                    operator.isType(KtRuleType.EXCL) -> "not"
                    else -> throw VkGrammarException()
                }
                VkExpression(VkFunctionExpression(name, VkFunctionType.OPERATOR, listOf(x)))
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: KtTree): VkExpression {
            return reduceRight(postfixUnaryExpression, { parsePrimaryExpression(it) }) { x, op ->
                val suffix = op.first()
                when (suffix.getTypeAsRule(VkGrammarException())){
                    KtRuleType.CALL_SUFFIX -> {
                        if (suffix.containsType(KtRuleType.ANNOTATED_LAMBDA)) {
                            throw VkParseException(suffix.linePos, "lambda expressions are not supported")
                        }
                        val valueArguments = suffix.getChildAs(KtRuleType.VALUE_ARGUMENTS, VkGrammarException()).children
                        val expressions = valueArguments.map { VkExpression(it.getChildAs(KtRuleType.EXPRESSION, VkGrammarException())) }
                        VkExpression(VkFunctionExpression("invoke", VkFunctionType.OPERATOR, listOf(x) + expressions))
                    }
                    KtRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.children.map { VkExpression(it) }
                        VkExpression(VkFunctionExpression("get", VkFunctionType.OPERATOR, listOf(x) + expressions))
                    }
                    KtRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.getChildAs(KtRuleType.SIMPLE_IDENTIFIER, VkGrammarException())
                        val identifier = (simpleIdentifier.first().node as KtToken).text
                        VkExpression(VkNavigationExpression(x, identifier))
                    }
                    else -> throw VkGrammarException()
                }
            }
        }

        private fun parsePrimaryExpression(primaryExpression: KtTree): VkExpression {
            return when(primaryExpression.first().getTypeAsRule(VkGrammarException())) {
                KtRuleType.PARENTHESIZED_EXPRESSION -> {
                    VkExpression(primaryExpression.first().first())
                }
                KtRuleType.SIMPLE_IDENTIFIER -> {
                    val identifier = (primaryExpression.first().first().node as KtToken).text
                    VkExpression(VkIdentifierExpression(identifier))
                }
                KtRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(primaryExpression.first())
                }
                KtRuleType.STRING_LITERAL -> {
                    parseStringLiteral(primaryExpression.first())
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

        private fun parseLiteralConstant(literalConstant: KtTree): VkExpression {
            val value = (literalConstant.first().node as KtToken).text
            return VkExpression(VkLiteralExpression(value))
        }

        private fun parseStringLiteral(literalConstant: KtTree): VkExpression {
            throw VkParseException(literalConstant.linePos, "string literals are not supported")
        }
    }
}