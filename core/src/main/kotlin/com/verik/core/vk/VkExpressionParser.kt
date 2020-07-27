package com.verik.core.vk

import com.verik.core.LinePosException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.kt.KtToken
import com.verik.core.kt.KtTokenType

// Copyright (c) 2020 Francis Wang

class VkExpressionParser {

    companion object {

        fun parse(expression: KtRule): VkExpression {
            val disjunction = expression.childAs(KtRuleType.DISJUNCTION)
            return parseDisjunction(disjunction)
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw LinePosException("rule node has no children", root.linePos)
            var x = map(root.children[0].asRule())
            for (child in root.children.drop(1)) {
                x = acc(x, map(child.asRule()))
            }
            return x
        }

        private fun reduce(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw LinePosException("rule node has no children", root.linePos)
            val iterator = root.children.iterator()
            var x = map(iterator.next().asRule())
            while (iterator.hasNext()) {
                val op = iterator.next().asRule()
                val y = map(iterator.next().asRule())
                x = acc(x, y, op)
            }
            return x
        }

        private fun reduceLeft(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw LinePosException("rule node has no children", root.linePos)
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0].asRule())
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child.asRule())
            }
            return x
        }

        private fun reduceRight(root: KtRule, map: (KtRule) -> VkExpression, acc: (VkExpression, KtRule) -> VkExpression): VkExpression {
            if (root.children.isEmpty()) throw LinePosException("rule node has no children", root.linePos)
            var x = map(root.children[0].asRule())
            for (child in root.children.drop(1)) {
                x = acc(x, child.asRule())
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
                val type = when (op.firstAsTokenType()) {
                    KtTokenType.EQEQ -> VkOperatorType.EQ
                    KtTokenType.EXCL_EQ -> VkOperatorType.NEQ
                    else -> throw LinePosException("equality operator expected", equality.linePos)
                }
                VkOperatorExpression(equality.linePos, type, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: KtRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    KtTokenType.LANGLE -> VkOperatorType.LT
                    KtTokenType.RANGLE -> VkOperatorType.GT
                    KtTokenType.LE -> VkOperatorType.LTE
                    KtTokenType.GE -> VkOperatorType.GTE
                    else -> throw LinePosException("comparison operator expected", comparison.linePos)
                }
                VkOperatorExpression(comparison.linePos, type, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: KtRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
                val type = when (op.type) {
                    KtRuleType.IN_OPERATOR-> VkOperatorType.IN
                    else -> throw LinePosException("in operator expected", infixOperation.linePos)
                }
                VkOperatorExpression(infixOperation.linePos, type, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: KtRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val identifier = op.firstAsTokenText()
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
                val type = when (op.firstAsTokenType()) {
                    KtTokenType.ADD -> VkOperatorType.ADD_TRU
                    KtTokenType.SUB -> VkOperatorType.SUB_TRU
                    else -> throw LinePosException("additive operator expected", additiveExpression.linePos)
                }
                VkOperatorExpression(additiveExpression.linePos, type, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: KtRule): VkExpression {
            val map = {it: KtRule -> parsePrefixUnaryExpression(it.firstAsRule().firstAsRule()) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    KtTokenType.MULT -> VkOperatorType.MUL_TRU
                    KtTokenType.MOD -> VkOperatorType.MOD
                    KtTokenType.DIV -> VkOperatorType.DIV
                    else -> throw LinePosException("multiplicative operator expected", multiplicativeExpression.linePos)
                }
                VkOperatorExpression(multiplicativeExpression.linePos, type, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: KtRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.firstAsRule().first()
                val type = when {
                    operator is KtToken && operator.type == KtTokenType.ADD -> VkOperatorType.UNARY_PLUS
                    operator is KtToken && operator.type == KtTokenType.SUB -> VkOperatorType.UNARY_MINUS
                    operator is KtRule && operator.type == KtRuleType.EXCL -> VkOperatorType.NOT
                    else -> throw LinePosException("prefix unary operator expected", prefixUnaryExpression.linePos)
                }
                VkOperatorExpression(prefixUnaryExpression.linePos, type, listOf(x))
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: KtRule): VkExpression {
            return reduceRight(postfixUnaryExpression, { parsePrimaryExpression(it) }) { x, op ->
                val suffix = op.firstAsRule()
                when (suffix.type) {
                    KtRuleType.CALL_SUFFIX -> {
                        val valueArguments = suffix.childrenAs(KtRuleType.VALUE_ARGUMENTS)
                                .flatMap { it.childrenAs(KtRuleType.VALUE_ARGUMENT) }
                        val expressions = valueArguments.map { VkExpression(it.childAs(KtRuleType.EXPRESSION)) }
                        val lambdaLiterals = suffix.childrenAs(KtRuleType.ANNOTATED_LAMBDA)
                                .map { it.childAs(KtRuleType.LAMBDA_LITERAL) }
                        val lambdaExpressions = lambdaLiterals.map { parseLambdaLiteral(it) }
                        VkCallableExpression(postfixUnaryExpression.linePos, x, expressions + lambdaExpressions)
                    }
                    KtRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.childrenAs(KtRuleType.EXPRESSION).map { VkExpression(it) }
                        VkOperatorExpression(postfixUnaryExpression.linePos, VkOperatorType.GET, listOf(x) + expressions)
                    }
                    KtRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.childAs(KtRuleType.SIMPLE_IDENTIFIER)
                        val identifier = simpleIdentifier.firstAsTokenText()
                        VkNavigationExpression(postfixUnaryExpression.linePos, x, identifier)
                    }
                    else -> throw LinePosException("postfix unary suffix expected", postfixUnaryExpression.linePos)
                }
            }
        }

        private fun parsePrimaryExpression(primaryExpression: KtRule): VkExpression {
            val child = primaryExpression.firstAsRule()
            return when(child.type) {
                KtRuleType.PARENTHESIZED_EXPRESSION -> {
                    VkExpression(child.firstAsRule())
                }
                KtRuleType.SIMPLE_IDENTIFIER -> {
                    VkIdentifierExpression(primaryExpression.linePos, child.firstAsTokenText())
                }
                KtRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                KtRuleType.STRING_LITERAL -> {
                    VkStringParser.parse(child)
                }
                KtRuleType.FUNCTION_LITERAL -> {
                    parseLambdaLiteral(child.firstAsRule())
                }
                KtRuleType.THIS_EXPRESSION -> {
                    throw LinePosException("this expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.SUPER_EXPRESSION -> {
                    throw LinePosException("super expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.IF_EXPRESSION -> {
                    parseIfExpression(child)
                }
                KtRuleType.WHEN_EXPRESSION -> {
                    throw LinePosException("when expressions are not supported", primaryExpression.linePos)
                }
                KtRuleType.JUMP_EXPRESSION -> {
                    throw LinePosException("jump expressions are not supported", primaryExpression.linePos)
                }
                else -> throw LinePosException("primary expression expected", primaryExpression.linePos)
            }
        }

        private fun parseLiteralConstant(literalConstant: KtRule): VkExpression {
            val value = when (val text = literalConstant.firstAsTokenText()) {
                "true" -> "1"
                "false" -> "0"
                else -> text
            }
            return VkLiteralExpression(literalConstant.linePos, value)
        }

        private fun parseLambdaLiteral(lambdaLiteral: KtRule): VkExpression {
            if (lambdaLiteral.containsType(KtRuleType.LAMBDA_PARAMETERS)) {
                throw LinePosException("lambda parameters not supported", lambdaLiteral.linePos)
            }
            val statements = lambdaLiteral
                    .childAs(KtRuleType.STATEMENTS)
                    .childrenAs(KtRuleType.STATEMENT)
                    .map { VkStatement(it) }
            return VkLambdaExpression(lambdaLiteral.linePos, statements)
        }

        private fun parseIfExpression(ifExpression: KtRule): VkExpression {
            val expression = VkExpression(ifExpression.childAs(KtRuleType.EXPRESSION))
            return if (ifExpression.containsType(KtTokenType.ELSE)) {
                if (ifExpression.children.size == 2) {
                    throw LinePosException("if and else body expected", ifExpression.linePos)
                }
                if (ifExpression.children.size == 3) {
                    if (ifExpression.children[1] is KtToken) {
                        throw LinePosException("if body expected", ifExpression.linePos)
                    } else throw LinePosException("else body expected", ifExpression.linePos)
                }
                val ifBody = parseControlStructureBody(ifExpression.children[1].asRule())
                val elseBody = parseControlStructureBody(ifExpression.children[3].asRule())
                VkOperatorExpression(ifExpression.linePos, VkOperatorType.IF_ELSE, listOf(expression, ifBody, elseBody))
            } else {
                if (ifExpression.children.size != 2) {
                    throw LinePosException("if body expected", ifExpression.linePos)
                }
                val ifBody = parseControlStructureBody(ifExpression.childAs(KtRuleType.CONTROL_STRUCTURE_BODY))
                VkOperatorExpression(ifExpression.linePos, VkOperatorType.IF, listOf(expression, ifBody))
            }
        }

        private fun parseControlStructureBody(controlStructureBody: KtRule): VkLambdaExpression {
            val blockOrStatement = controlStructureBody.firstAsRule()
            val statements = when (blockOrStatement.type) {
                KtRuleType.BLOCK -> {
                    blockOrStatement.firstAsRule().childrenAs(KtRuleType.STATEMENT).map { VkStatement(it) }
                }
                KtRuleType.STATEMENT -> {
                    listOf(VkStatement(blockOrStatement))
                }
                else -> throw LinePosException("block or statement expected", blockOrStatement.linePos)
            }
            return VkLambdaExpression(controlStructureBody.linePos, statements)
        }
    }
}