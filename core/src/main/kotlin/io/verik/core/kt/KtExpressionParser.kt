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

package io.verik.core.kt

import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlToken
import io.verik.core.al.AlTokenType
import io.verik.core.main.LineException

object KtExpressionParser {

    fun parse(expression: AlRule): KtExpression {
        return parseDisjunction(expression.childAs(AlRuleType.DISJUNCTION))
    }

    private fun reduce(
            root: AlRule,
            map: (AlRule) -> KtExpression,
            acc: (KtExpression, KtExpression) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root)
        }
        var x = map(root.children[0].asRule())
        for (child in root.children.drop(1)) {
            x = acc(x, map(child.asRule()))
        }
        return x
    }

    private fun reduceOp(
            root: AlRule,
            map: (AlRule) -> KtExpression,
            acc: (KtExpression, KtExpression, AlRule) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root)
        }
        val iterator = root.children.iterator()
        var x = map(iterator.next().asRule())
        while (iterator.hasNext()) {
            val op = iterator.next().asRule()
            if (!iterator.hasNext()) {
                throw LineException("expression expected", root)
            }
            val y = map(iterator.next().asRule())
            x = acc(x, y, op)
        }
        return x
    }

    private fun reduceLeft(
            root: AlRule,
            map: (AlRule) -> KtExpression,
            acc: (KtExpression, AlRule) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root)
        }
        val reversedChildren = root.children.reversed()
        var x = map(reversedChildren[0].asRule())
        for (child in reversedChildren.drop(1)) {
            x = acc(x, child.asRule())
        }
        return x
    }

    private fun parseDisjunction(disjunction: AlRule): KtExpression {
        return reduce(disjunction, { parseConjunction(it) }) { x, y ->
            KtExpressionOperator(
                    disjunction.line,
                    x,
                    KtOperatorIdentifier.OR,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseConjunction(conjunction: AlRule): KtExpression {
        return reduce(conjunction, { parseEquality(it) }) { x, y ->
            KtExpressionOperator(
                    conjunction.line,
                    x,
                    KtOperatorIdentifier.AND,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseEquality(equality: AlRule): KtExpression {
        return reduceOp(equality, { parseComparison(it) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.EQEQ -> KtOperatorIdentifier.EQ
                AlTokenType.EXCL_EQ -> KtOperatorIdentifier.NOT_EQ
                else -> throw LineException("equality operator expected", equality)
            }
            KtExpressionOperator(
                    equality.line,
                    x,
                    identifier,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseComparison(comparison: AlRule): KtExpression {
        return reduceOp(comparison, { parseInfixOperation(it) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.LANGLE -> KtOperatorIdentifier.LT
                AlTokenType.RANGLE -> KtOperatorIdentifier.GT
                AlTokenType.LE -> KtOperatorIdentifier.LT_EQ
                AlTokenType.GE -> KtOperatorIdentifier.GT_EQ
                else -> throw LineException("comparison operator expected", comparison)
            }
            KtExpressionOperator(
                    comparison.line,
                    x,
                    identifier,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseInfixOperation(infixOperation: AlRule): KtExpression {
        return reduceOp(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.IN -> KtOperatorIdentifier.IN
                AlTokenType.NOT_IN -> KtOperatorIdentifier.NOT_IN
                else -> throw LineException("infix operator expected", infixOperation)
            }
            KtExpressionOperator(
                    infixOperation.line,
                    x,
                    identifier,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseInfixFunctionCall(infixFunctionCall: AlRule): KtExpression {
        if (infixFunctionCall.children.isEmpty()) {
            throw LineException("rule node has no children", infixFunctionCall)
        }
        val iterator = infixFunctionCall.children.iterator()
        var expression = parseRangeExpression(iterator.next().asRule())
        while (iterator.hasNext()) {
            val identifier = iterator
                    .next()
                    .asRule()
                    .firstAsTokenText()
            val operatorIdentifier = KtOperatorIdentifier.infixIdentifier(identifier, infixFunctionCall)

            if (!iterator.hasNext()) {
                throw LineException("expression expected", infixFunctionCall)
            }
            val argOrBlock = iterator.next().asRule()
            val block = parseInfixFunctionCallBlock(argOrBlock)
            if (block != null) {
                expression = KtExpressionOperator(
                        infixFunctionCall.line,
                        expression,
                        operatorIdentifier,
                        listOf(),
                        listOf(block)
                )
            } else {
                val arg = parseRangeExpression(argOrBlock)
                expression = KtExpressionOperator(
                        infixFunctionCall.line,
                        expression,
                        operatorIdentifier,
                        listOf(arg),
                        listOf()
                )
            }
        }
        return expression
    }

    private fun parseInfixFunctionCallBlock(rangeExpression: AlRule): KtBlock? {
        val primaryExpression = rangeExpression
                .let { if (it.children.size == 1) it.childAs(AlRuleType.ADDITIVE_EXPRESSION) else null }
                ?.let { if (it.children.size == 1) it.childAs(AlRuleType.MULTIPLICATIVE_EXPRESSION) else null }
                ?.let { if (it.children.size == 1) it.childAs(AlRuleType.AS_EXPRESSION) else null }
                ?.let { if (it.firstAsRule().children.size == 1) it.firstAsRule().childAs(AlRuleType.PREFIX_UNARY_EXPRESSION) else null }
                ?.let { if (it.children.size == 1) it.childAs(AlRuleType.POSTFIX_UNARY_EXPRESSION) else null }
                ?.let { if (it.children.size == 1) it.childAs(AlRuleType.PRIMARY_EXPRESSION) else null }
        return if (primaryExpression != null && primaryExpression.containsType(AlRuleType.FUNCTION_LITERAL)) {
            return primaryExpression
                    .childAs(AlRuleType.FUNCTION_LITERAL)
                    .childAs(AlRuleType.LAMBDA_LITERAL)
                    .let { parseLambdaLiteral(it) }
        } else null
    }

    private fun parseRangeExpression(rangeExpression: AlRule): KtExpression {
        return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
            KtExpressionOperator(
                    rangeExpression.line,
                    x,
                    KtOperatorIdentifier.RANGE,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseAdditiveExpression(additiveExpression: AlRule): KtExpression {
        return reduceOp(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.ADD -> KtOperatorIdentifier.ADD
                AlTokenType.SUB -> KtOperatorIdentifier.SUB
                else -> throw LineException("additive operator expected", additiveExpression)
            }
            KtExpressionOperator(
                    additiveExpression.line,
                    x,
                    identifier,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule): KtExpression {
        return reduceOp(multiplicativeExpression, { parsePrefixUnaryExpression(it.firstAsRule().firstAsRule()) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.MULT -> KtOperatorIdentifier.MUL
                AlTokenType.MOD -> KtOperatorIdentifier.MOD
                AlTokenType.DIV -> KtOperatorIdentifier.DIV
                else -> throw LineException("multiplicative operator expected", multiplicativeExpression)
            }
            KtExpressionOperator(
                    multiplicativeExpression.line,
                    x,
                    identifier,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parsePrefixUnaryExpression(prefixUnaryExpression: AlRule): KtExpression {
        return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
            val operator = op.firstAsRule().first()
            val identifier = when {
                operator is AlToken && operator.type == AlTokenType.ADD -> KtOperatorIdentifier.UNARY_ADD
                operator is AlToken && operator.type == AlTokenType.SUB -> KtOperatorIdentifier.UNARY_SUB
                operator is AlRule && operator.type == AlRuleType.EXCL -> KtOperatorIdentifier.NOT
                else -> throw LineException("prefix unary operator expected", prefixUnaryExpression)
            }
            KtExpressionOperator(
                    prefixUnaryExpression.line,
                    x,
                    identifier,
                    listOf(),
                    listOf()
            )
        }
    }

    private fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule): KtExpression {
        val primaryExpression = KtPrimaryExpressionParser.parse(postfixUnaryExpression.childAs(AlRuleType.PRIMARY_EXPRESSION))
        var expression: KtExpression? = null
        var identifier: String? = null
        if (primaryExpression is KtExpressionProperty && primaryExpression.target == null) {
            identifier = primaryExpression.identifier
        } else {
            expression = primaryExpression
        }
        val suffixes = postfixUnaryExpression.childrenAs(AlRuleType.POSTFIX_UNARY_SUFFIX).map { it.firstAsRule() }
        for (suffix in suffixes) {
            if (suffix.type == AlRuleType.CALL_SUFFIX) {
                if (identifier != null) {
                    val args = suffix
                            .childrenAs(AlRuleType.VALUE_ARGUMENTS)
                            .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                            .map { it.childAs(AlRuleType.EXPRESSION) }
                            .map { KtExpression(it) }
                    if (suffix.containsType(AlRuleType.ANNOTATED_LAMBDA)) {
                        val operatorIdentifier = KtOperatorIdentifier.lambdaIdentifier(identifier, postfixUnaryExpression)
                        val block = suffix
                                .childAs(AlRuleType.ANNOTATED_LAMBDA)
                                .childAs(AlRuleType.LAMBDA_LITERAL)
                                .let { parseLambdaLiteral(it) }
                        expression = KtExpressionOperator(
                                postfixUnaryExpression.line,
                                expression,
                                operatorIdentifier,
                                args,
                                listOf(block)
                        )
                    } else {
                        expression = KtExpressionFunction(
                                postfixUnaryExpression.line,
                                expression,
                                identifier,
                                args
                        )
                    }
                    identifier = null
                } else {
                    throw LineException("illegal call target", postfixUnaryExpression)
                }
            } else {
                if (identifier != null) {
                    expression = KtExpressionProperty(
                            postfixUnaryExpression.line,
                            expression,
                            identifier
                    )
                    identifier = null
                }
                when (suffix.type) {
                    AlRuleType.INDEXING_SUFFIX -> {
                        val args = suffix.childrenAs(AlRuleType.EXPRESSION).map { KtExpression(it) }
                        expression = KtExpressionOperator(
                                postfixUnaryExpression.line,
                                expression,
                                KtOperatorIdentifier.GET,
                                args,
                                listOf()
                        )
                    }
                    AlRuleType.NAVIGATION_SUFFIX -> {
                        identifier = suffix.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                    }
                    else -> throw LineException("postfix unary suffix expected", postfixUnaryExpression)
                }
            }
        }
        if (identifier != null) {
            expression = KtExpressionProperty(
                    postfixUnaryExpression.line,
                    expression,
                    identifier
            )
        }
        return expression!!
    }

    private fun parseLambdaLiteral(lambdaLiteral: AlRule): KtBlock {
        if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
            throw LineException("lambda parameters not supported", lambdaLiteral)
        }
        val statements = lambdaLiteral
                .childAs(AlRuleType.STATEMENTS)
                .childrenAs(AlRuleType.STATEMENT)
                .map { KtStatement(it) }
        return KtBlock(lambdaLiteral.line, statements)
    }
}