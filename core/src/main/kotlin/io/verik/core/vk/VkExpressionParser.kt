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

package io.verik.core.vk

import io.verik.core.FileLineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlToken
import io.verik.core.al.AlTokenType

class VkExpressionParser {

    companion object {

        fun parse(expression: AlRule): VkExpression {
            val disjunction = expression.childAs(AlRuleType.DISJUNCTION)
            return parseDisjunction(disjunction)
        }

        private fun reduce(
                root: AlRule,
                map: (AlRule) -> VkExpression,
                acc: (VkExpression, VkExpression) -> VkExpression
        ): VkExpression {
            if (root.children.isEmpty()) throw FileLineException("rule node has no children", root.fileLine)
            var x = map(root.children[0].asRule())
            for (child in root.children.drop(1)) {
                x = acc(x, map(child.asRule()))
            }
            return x
        }

        private fun reduce(
                root: AlRule,
                map: (AlRule) -> VkExpression,
                acc: (VkExpression, VkExpression, AlRule) -> VkExpression
        ): VkExpression {
            if (root.children.isEmpty()) throw FileLineException("rule node has no children", root.fileLine)
            val iterator = root.children.iterator()
            var x = map(iterator.next().asRule())
            while (iterator.hasNext()) {
                val op = iterator.next().asRule()
                val y = map(iterator.next().asRule())
                x = acc(x, y, op)
            }
            return x
        }

        private fun reduceLeft(
                root: AlRule,
                map: (AlRule) -> VkExpression,
                acc: (VkExpression, AlRule) -> VkExpression
        ): VkExpression {
            if (root.children.isEmpty()) throw FileLineException("rule node has no children", root.fileLine)
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0].asRule())
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child.asRule())
            }
            return x
        }

        private fun reduceRight(
                root: AlRule,
                map: (AlRule) -> VkExpression,
                acc: (VkExpression, AlRule) -> VkExpression
        ): VkExpression {
            if (root.children.isEmpty()) throw FileLineException("rule node has no children", root.fileLine)
            var x = map(root.children[0].asRule())
            for (child in root.children.drop(1)) {
                x = acc(x, child.asRule())
            }
            return x
        }

        private fun parseDisjunction(disjunction: AlRule): VkExpression {
            return reduce(disjunction, { parseConjunction(it) }) { x, y ->
                VkExpressionOperator(disjunction.fileLine, VkOperatorType.OR, listOf(x, y))
            }
        }

        private fun parseConjunction(conjunction: AlRule): VkExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                VkExpressionOperator(conjunction.fileLine, VkOperatorType.AND, listOf(x, y))
            }
        }

        private fun parseEquality(equality: AlRule): VkExpression {
            return reduce(equality, { parseComparison(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.EQEQ -> VkOperatorType.EQ
                    AlTokenType.EXCL_EQ -> VkOperatorType.NEQ
                    else -> throw FileLineException("equality operator expected", equality.fileLine)
                }
                VkExpressionOperator(equality.fileLine, type, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: AlRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.LANGLE -> VkOperatorType.LT
                    AlTokenType.RANGLE -> VkOperatorType.GT
                    AlTokenType.LE -> VkOperatorType.LTE
                    AlTokenType.GE -> VkOperatorType.GTE
                    else -> throw FileLineException("comparison operator expected", comparison.fileLine)
                }
                VkExpressionOperator(comparison.fileLine, type, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: AlRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
                val type = when (op.type) {
                    AlRuleType.IN_OPERATOR-> VkOperatorType.IN
                    else -> throw FileLineException("in operator expected", infixOperation.fileLine)
                }
                VkExpressionOperator(infixOperation.fileLine, type, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: AlRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val identifier = op.firstAsTokenText()
                val type = VkOperatorType.infixType(identifier, infixFunctionCall.fileLine)
                VkExpressionOperator(infixFunctionCall.fileLine, type, listOf(x, y))
            }
        }

        private fun parseRangeExpression(rangeExpression: AlRule): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkExpressionOperator(rangeExpression.fileLine, VkOperatorType.RANGE_TO, listOf(x, y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: AlRule): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.ADD -> VkOperatorType.ADD_TRU
                    AlTokenType.SUB -> VkOperatorType.SUB_TRU
                    else -> throw FileLineException("additive operator expected", additiveExpression.fileLine)
                }
                VkExpressionOperator(additiveExpression.fileLine, type, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule): VkExpression {
            val map = {it: AlRule -> parsePrefixUnaryExpression(it.firstAsRule().firstAsRule()) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.MULT -> VkOperatorType.MUL_TRU
                    AlTokenType.MOD -> VkOperatorType.MOD
                    AlTokenType.DIV -> VkOperatorType.DIV
                    else -> throw FileLineException("multiplicative operator expected", multiplicativeExpression.fileLine)
                }
                VkExpressionOperator(multiplicativeExpression.fileLine, type, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: AlRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.firstAsRule().first()
                val type = when {
                    operator is AlToken && operator.type == AlTokenType.ADD -> VkOperatorType.UNARY_PLUS
                    operator is AlToken && operator.type == AlTokenType.SUB -> VkOperatorType.UNARY_MINUS
                    operator is AlRule && operator.type == AlRuleType.EXCL -> VkOperatorType.NOT
                    else -> throw FileLineException("prefix unary operator expected", prefixUnaryExpression.fileLine)
                }
                VkExpressionOperator(prefixUnaryExpression.fileLine, type, listOf(x))
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule): VkExpression {
            return reduceRight(postfixUnaryExpression, { parsePrimaryExpression(it) }) { x, op ->
                val suffix = op.firstAsRule()
                when (suffix.type) {
                    AlRuleType.CALL_SUFFIX -> {
                        val valueArguments = suffix.childrenAs(AlRuleType.VALUE_ARGUMENTS)
                                .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                        val expressions = valueArguments.map { VkExpression(it.childAs(AlRuleType.EXPRESSION)) }
                        val lambdaLiterals = suffix.childrenAs(AlRuleType.ANNOTATED_LAMBDA)
                                .map { it.childAs(AlRuleType.LAMBDA_LITERAL) }
                        val lambdaExpressions = lambdaLiterals.map { parseLambdaLiteral(it) }
                        VkExpressionCallable(postfixUnaryExpression.fileLine, x, expressions + lambdaExpressions)
                    }
                    AlRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.childrenAs(AlRuleType.EXPRESSION).map { VkExpression(it) }
                        VkExpressionOperator(postfixUnaryExpression.fileLine, VkOperatorType.GET, listOf(x) + expressions)
                    }
                    AlRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.childAs(AlRuleType.SIMPLE_IDENTIFIER)
                        val identifier = simpleIdentifier.firstAsTokenText()
                        VkExpressionNavigation(postfixUnaryExpression.fileLine, x, identifier)
                    }
                    else -> throw FileLineException("postfix unary suffix expected", postfixUnaryExpression.fileLine)
                }
            }
        }

        private fun parsePrimaryExpression(primaryExpression: AlRule): VkExpression {
            val child = primaryExpression.firstAsRule()
            return when(child.type) {
                AlRuleType.PARENTHESIZED_EXPRESSION -> {
                    VkExpression(child.firstAsRule())
                }
                AlRuleType.SIMPLE_IDENTIFIER -> {
                    VkExpressionIdentifier(primaryExpression.fileLine, child.firstAsTokenText())
                }
                AlRuleType.LITERAL_CONSTANT -> {
                    parseLiteralConstant(child)
                }
                AlRuleType.STRING_LITERAL -> {
                    VkStringParser.parse(child)
                }
                AlRuleType.FUNCTION_LITERAL -> {
                    parseLambdaLiteral(child.firstAsRule())
                }
                AlRuleType.THIS_EXPRESSION -> {
                    throw FileLineException("this expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    throw FileLineException("super expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.IF_EXPRESSION -> {
                    parseIfExpression(child)
                }
                AlRuleType.WHEN_EXPRESSION -> {
                    throw FileLineException("when expressions are not supported", primaryExpression.fileLine)
                }
                AlRuleType.JUMP_EXPRESSION -> {
                    throw FileLineException("jump expressions are not supported", primaryExpression.fileLine)
                }
                else -> throw FileLineException("primary expression expected", primaryExpression.fileLine)
            }
        }

        private fun parseLiteralConstant(literalConstant: AlRule): VkExpression {
            val value = when (val text = literalConstant.firstAsTokenText()) {
                "true" -> "1"
                "false" -> "0"
                else -> text
            }
            return VkExpressionLiteral(literalConstant.fileLine, value)
        }

        private fun parseLambdaLiteral(lambdaLiteral: AlRule): VkExpression {
            if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
                throw FileLineException("lambda parameters not supported", lambdaLiteral.fileLine)
            }
            val statements = lambdaLiteral
                    .childAs(AlRuleType.STATEMENTS)
                    .childrenAs(AlRuleType.STATEMENT)
                    .map { VkStatement(it) }
            return VkExpressionLambda(lambdaLiteral.fileLine, statements)
        }

        private fun parseIfExpression(ifExpression: AlRule): VkExpression {
            val expression = VkExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
            return if (ifExpression.containsType(AlTokenType.ELSE)) {
                if (ifExpression.children.size == 2) {
                    throw FileLineException("if and else body expected", ifExpression.fileLine)
                }
                if (ifExpression.children.size == 3) {
                    if (ifExpression.children[1] is AlToken) {
                        throw FileLineException("if body expected", ifExpression.fileLine)
                    } else throw FileLineException("else body expected", ifExpression.fileLine)
                }
                val ifBody = parseControlStructureBody(ifExpression.children[1].asRule())
                val elseBody = parseControlStructureBody(ifExpression.children[3].asRule())
                VkExpressionOperator(ifExpression.fileLine, VkOperatorType.IF_ELSE, listOf(expression, ifBody, elseBody))
            } else {
                if (ifExpression.children.size != 2) {
                    throw FileLineException("if body expected", ifExpression.fileLine)
                }
                val ifBody = parseControlStructureBody(ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY))
                VkExpressionOperator(ifExpression.fileLine, VkOperatorType.IF, listOf(expression, ifBody))
            }
        }

        private fun parseControlStructureBody(controlStructureBody: AlRule): VkExpressionLambda {
            val blockOrStatement = controlStructureBody.firstAsRule()
            val statements = when (blockOrStatement.type) {
                AlRuleType.BLOCK -> {
                    blockOrStatement.firstAsRule().childrenAs(AlRuleType.STATEMENT).map { VkStatement(it) }
                }
                AlRuleType.STATEMENT -> {
                    listOf(VkStatement(blockOrStatement))
                }
                else -> throw FileLineException("block or statement expected", blockOrStatement.fileLine)
            }
            return VkExpressionLambda(controlStructureBody.fileLine, statements)
        }
    }
}