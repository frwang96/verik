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
                VkOperatorExpression(disjunction.linePos, VkOperatorType.OR, listOf(x, y))
            }
        }

        private fun parseConjunction(conjunction: KtRule): VkExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                VkOperatorExpression(conjunction.linePos, VkOperatorType.AND, listOf(x, y))
            }
        }

        private fun parseEquality(equality: KtRule): VkExpression {
            return reduce(equality, { parseComparison(it) }) { x, y, op ->
                val type = when (op.getFirstAsTokenType()) {
                    KtTokenType.EQEQ -> VkOperatorType.EQ
                    KtTokenType.EXCL_EQ -> VkOperatorType.NEQ
                    else -> throw KtGrammarException("equality operator expected", equality.linePos)
                }
                VkOperatorExpression(equality.linePos, type, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: KtRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val type = when (op.getFirstAsTokenType()) {
                    KtTokenType.LANGLE -> VkOperatorType.LT
                    KtTokenType.RANGLE -> VkOperatorType.GT
                    KtTokenType.LE -> VkOperatorType.LTE
                    KtTokenType.GE -> VkOperatorType.GTE
                    else -> throw KtGrammarException("comparison operator expected", comparison.linePos)
                }
                VkOperatorExpression(comparison.linePos, type, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: KtRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.getFirstAsRule()) }) { x, y, op ->
                val type = when (op.type) {
                    KtRuleType.IN_OPERATOR-> VkOperatorType.IN
                    else -> throw KtGrammarException("in operator expected", infixOperation.linePos)
                }
                VkOperatorExpression(infixOperation.linePos, type, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: KtRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val identifier = op.getFirstAsTokenText()
                val type = VkOperatorType.infixType(identifier, infixFunctionCall.linePos)
                VkOperatorExpression(infixFunctionCall.linePos, type, listOf(x, y))
            }
        }

        private fun parseRangeExpression(rangeExpression: KtRule): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkOperatorExpression(rangeExpression.linePos, VkOperatorType.RANGE_TO, listOf(x, y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: KtRule): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val type = when (op.getFirstAsTokenType()) {
                    KtTokenType.ADD -> VkOperatorType.ADD_TRU
                    KtTokenType.SUB -> VkOperatorType.SUB_TRU
                    else -> throw VkParseException("additive operator expected", additiveExpression.linePos)
                }
                VkOperatorExpression(additiveExpression.linePos, type, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: KtRule): VkExpression {
            val map = {it: KtRule -> parsePrefixUnaryExpression(it.getFirstAsRule().getFirstAsRule()) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val type = when (op.getFirstAsTokenType()) {
                    KtTokenType.MULT -> VkOperatorType.MUL_TRU
                    KtTokenType.MOD -> VkOperatorType.MOD
                    KtTokenType.DIV -> VkOperatorType.DIV
                    else -> throw VkParseException("multiplicative operator expected", multiplicativeExpression.linePos)
                }
                VkOperatorExpression(multiplicativeExpression.linePos, type, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: KtRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.getFirstAsRule().getFirst()
                val type = when {
                    operator is KtToken && operator.type == KtTokenType.ADD -> VkOperatorType.UNARY_PLUS
                    operator is KtToken && operator.type == KtTokenType.SUB -> VkOperatorType.UNARY_MINUS
                    operator is KtRule && operator.type == KtRuleType.EXCL -> VkOperatorType.NOT
                    else -> throw VkParseException("prefix unary operator expected", prefixUnaryExpression.linePos)
                }
                VkOperatorExpression(prefixUnaryExpression.linePos, type, listOf(x))
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
                        VkCallableExpression(postfixUnaryExpression.linePos, x, expressions)
                    }
                    KtRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.getChildrenAs(KtRuleType.EXPRESSION).map { VkExpression(it) }
                        VkOperatorExpression(postfixUnaryExpression.linePos, VkOperatorType.GET, listOf(x) + expressions)
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
                    VkIdentifierExpression(primaryExpression.linePos, child.getFirstAsTokenText())
                }
                KtRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                KtRuleType.STRING_LITERAL -> {
                    parseStringLiteral(child)
                }
                KtRuleType.FUNCTION_LITERAL -> {
                    parseFunctionLiteral(child)
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

        private fun parseStringLiteral(stringLiteral: KtRule): VkExpression {
            throw VkParseException("string literals are not supported", stringLiteral.linePos)
        }

        private fun parseFunctionLiteral(functionLiteral: KtRule): VkExpression {
            val lambdaLiteral = functionLiteral.getFirstAsRule()
            if (lambdaLiteral.containsType(KtRuleType.LAMBDA_PARAMETERS)) {
                throw VkParseException("lambda parameters not supported", functionLiteral.linePos)
            }
            val statements = lambdaLiteral
                    .getChildAs(KtRuleType.STATEMENTS)
                    .getChildrenAs(KtRuleType.STATEMENT)
                    .map { VkStatement(it) }
            return VkLambdaExpression(functionLiteral.linePos, statements)
        }
    }
}