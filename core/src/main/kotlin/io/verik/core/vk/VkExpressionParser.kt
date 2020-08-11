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

import io.verik.core.main.LineException
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
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
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
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
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
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
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
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
            var x = map(root.children[0].asRule())
            for (child in root.children.drop(1)) {
                x = acc(x, child.asRule())
            }
            return x
        }

        private fun parseDisjunction(disjunction: AlRule): VkExpression {
            return reduce(disjunction, { parseConjunction(it) }) { x, y ->
                VkExpressionOperator(disjunction.line, VkOperatorType.OR, listOf(x, y))
            }
        }

        private fun parseConjunction(conjunction: AlRule): VkExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                VkExpressionOperator(conjunction.line, VkOperatorType.AND, listOf(x, y))
            }
        }

        private fun parseEquality(equality: AlRule): VkExpression {
            return reduce(equality, { parseComparison(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.EQEQ -> VkOperatorType.EQ
                    AlTokenType.EXCL_EQ -> VkOperatorType.NEQ
                    else -> throw LineException("equality operator expected", equality)
                }
                VkExpressionOperator(equality.line, type, listOf(x, y))
            }
        }

        private fun parseComparison(comparison: AlRule): VkExpression {
            return reduce(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.LANGLE -> VkOperatorType.LT
                    AlTokenType.RANGLE -> VkOperatorType.GT
                    AlTokenType.LE -> VkOperatorType.LTE
                    AlTokenType.GE -> VkOperatorType.GTE
                    else -> throw LineException("comparison operator expected", comparison)
                }
                VkExpressionOperator(comparison.line, type, listOf(x, y))
            }
        }

        private fun parseInfixOperation(infixOperation: AlRule): VkExpression {
            return reduce(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
                val type = when (op.type) {
                    AlRuleType.IN_OPERATOR-> VkOperatorType.IN
                    else -> throw LineException("in operator expected", infixOperation)
                }
                VkExpressionOperator(infixOperation.line, type, listOf(x, y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: AlRule): VkExpression {
            return reduce(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val identifier = op.firstAsTokenText()
                val type = VkOperatorType.infixType(identifier, infixFunctionCall.line)
                VkExpressionOperator(infixFunctionCall.line, type, listOf(x, y))
            }
        }

        private fun parseRangeExpression(rangeExpression: AlRule): VkExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                VkExpressionOperator(rangeExpression.line, VkOperatorType.RANGE_TO, listOf(x, y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: AlRule): VkExpression {
            return reduce(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.ADD -> VkOperatorType.ADD_TRU
                    AlTokenType.SUB -> VkOperatorType.SUB_TRU
                    else -> throw LineException("additive operator expected", additiveExpression)
                }
                VkExpressionOperator(additiveExpression.line, type, listOf(x, y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule): VkExpression {
            val map = {it: AlRule -> parsePrefixUnaryExpression(it.firstAsRule().firstAsRule()) }
            return reduce(multiplicativeExpression, map) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.MULT -> VkOperatorType.MUL_TRU
                    AlTokenType.MOD -> VkOperatorType.MOD
                    AlTokenType.DIV -> VkOperatorType.DIV
                    else -> throw LineException("multiplicative operator expected", multiplicativeExpression)
                }
                VkExpressionOperator(multiplicativeExpression.line, type, listOf(x, y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: AlRule): VkExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.firstAsRule().first()
                val type = when {
                    operator is AlToken && operator.type == AlTokenType.ADD -> VkOperatorType.UNARY_PLUS
                    operator is AlToken && operator.type == AlTokenType.SUB -> VkOperatorType.UNARY_MINUS
                    operator is AlRule && operator.type == AlRuleType.EXCL -> VkOperatorType.NOT
                    else -> throw LineException("prefix unary operator expected", prefixUnaryExpression)
                }
                VkExpressionOperator(prefixUnaryExpression.line, type, listOf(x))
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
                        VkExpressionCallable(postfixUnaryExpression.line, x, expressions + lambdaExpressions)
                    }
                    AlRuleType.INDEXING_SUFFIX -> {
                        val expressions = suffix.childrenAs(AlRuleType.EXPRESSION).map { VkExpression(it) }
                        VkExpressionOperator(postfixUnaryExpression.line, VkOperatorType.GET, listOf(x) + expressions)
                    }
                    AlRuleType.NAVIGATION_SUFFIX -> {
                        val simpleIdentifier = suffix.childAs(AlRuleType.SIMPLE_IDENTIFIER)
                        val identifier = simpleIdentifier.firstAsTokenText()
                        VkExpressionNavigation(postfixUnaryExpression.line, x, identifier)
                    }
                    else -> throw LineException("postfix unary suffix expected", postfixUnaryExpression)
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
                    VkExpressionIdentifier(primaryExpression.line, child.firstAsTokenText())
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
                    throw LineException("this expressions are not supported", primaryExpression)
                }
                AlRuleType.SUPER_EXPRESSION -> {
                    throw LineException("super expressions are not supported", primaryExpression)
                }
                AlRuleType.IF_EXPRESSION -> {
                    parseIfExpression(child)
                }
                AlRuleType.WHEN_EXPRESSION -> {
                    throw LineException("when expressions are not supported", primaryExpression)
                }
                AlRuleType.JUMP_EXPRESSION -> {
                    throw LineException("jump expressions are not supported", primaryExpression)
                }
                else -> throw LineException("primary expression expected", primaryExpression)
            }
        }

        private fun parseLiteralConstant(literalConstant: AlRule): VkExpression {
            val value = when (val text = literalConstant.firstAsTokenText()) {
                "true" -> "1"
                "false" -> "0"
                else -> text
            }
            return VkExpressionLiteral(literalConstant.line, value)
        }

        private fun parseLambdaLiteral(lambdaLiteral: AlRule): VkExpression {
            if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
                throw LineException("lambda parameters not supported", lambdaLiteral)
            }
            val statements = lambdaLiteral
                    .childAs(AlRuleType.STATEMENTS)
                    .childrenAs(AlRuleType.STATEMENT)
                    .map { VkStatement(it) }
            return VkExpressionLambda(lambdaLiteral.line, statements)
        }

        private fun parseIfExpression(ifExpression: AlRule): VkExpression {
            val expression = VkExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
            return if (ifExpression.containsType(AlTokenType.ELSE)) {
                if (ifExpression.children.size == 2) {
                    throw LineException("if and else body expected", ifExpression)
                }
                if (ifExpression.children.size == 3) {
                    if (ifExpression.children[1] is AlToken) {
                        throw LineException("if body expected", ifExpression)
                    } else throw LineException("else body expected", ifExpression)
                }
                val ifBody = parseControlStructureBody(ifExpression.children[1].asRule())
                val elseBody = parseControlStructureBody(ifExpression.children[3].asRule())
                VkExpressionOperator(ifExpression.line, VkOperatorType.IF_ELSE, listOf(expression, ifBody, elseBody))
            } else {
                if (ifExpression.children.size != 2) {
                    throw LineException("if body expected", ifExpression)
                }
                val ifBody = parseControlStructureBody(ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY))
                VkExpressionOperator(ifExpression.line, VkOperatorType.IF, listOf(expression, ifBody))
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
                else -> throw LineException("block or statement expected", blockOrStatement)
            }
            return VkExpressionLambda(controlStructureBody.line, statements)
        }
    }
}