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

package verik.core.kt.parse

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.al.AlTokenType
import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtBlock
import verik.core.kt.KtExpression
import verik.core.kt.KtExpressionFunction
import verik.core.kt.KtExpressionOperator
import verik.core.lang.LangSymbol.OPERATOR_ADD
import verik.core.lang.LangSymbol.OPERATOR_AND
import verik.core.lang.LangSymbol.OPERATOR_DIV
import verik.core.lang.LangSymbol.OPERATOR_GT
import verik.core.lang.LangSymbol.OPERATOR_GT_EQ
import verik.core.lang.LangSymbol.OPERATOR_IN
import verik.core.lang.LangSymbol.OPERATOR_LT
import verik.core.lang.LangSymbol.OPERATOR_LT_EQ
import verik.core.lang.LangSymbol.OPERATOR_MOD
import verik.core.lang.LangSymbol.OPERATOR_MUL
import verik.core.lang.LangSymbol.OPERATOR_NOT_IN
import verik.core.lang.LangSymbol.OPERATOR_OR
import verik.core.lang.LangSymbol.OPERATOR_RANGE
import verik.core.lang.LangSymbol.OPERATOR_SUB
import verik.core.lang.LangSymbol.OPERATOR_WITH

object KtExpressionParser {

    fun parse(expression: AlRule): KtExpression {
        return parseDisjunction(expression.childAs(AlRuleType.DISJUNCTION))
    }

    private fun reduceRight(
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

    private fun parseDisjunction(disjunction: AlRule): KtExpression {
        return reduceRight(disjunction, { parseConjunction(it) }) { x, y ->
            KtExpressionOperator(
                    disjunction.line,
                    null,
                    OPERATOR_OR,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseConjunction(conjunction: AlRule): KtExpression {
        return reduceRight(conjunction, { parseComparison(it.firstAsRule()) }) { x, y ->
            KtExpressionOperator(
                    conjunction.line,
                    null,
                    OPERATOR_AND,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseComparison(comparison: AlRule): KtExpression {
        return reduceOp(comparison, { parseInfixOperation(it) }) { x, y, op ->
            val operator = when (op.firstAsTokenType()) {
                AlTokenType.LANGLE -> OPERATOR_LT
                AlTokenType.RANGLE -> OPERATOR_GT
                AlTokenType.LE -> OPERATOR_LT_EQ
                AlTokenType.GE -> OPERATOR_GT_EQ
                else -> throw LineException("comparison operator expected", comparison)
            }
            KtExpressionOperator(
                    comparison.line,
                    null,
                    operator,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseInfixOperation(infixOperation: AlRule): KtExpression {
        return reduceOp(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.IN -> OPERATOR_IN
                AlTokenType.NOT_IN -> OPERATOR_NOT_IN
                else -> throw LineException("infix operator expected", infixOperation)
            }
            KtExpressionOperator(
                    infixOperation.line,
                    null,
                    identifier,
                    x,
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

            if (!iterator.hasNext()) {
                throw LineException("expression expected", infixFunctionCall)
            }
            val argOrBlock = iterator.next().asRule()
            val block = parseInfixFunctionCallBlock(argOrBlock)
            if (block != null) {
                expression = KtExpressionOperator(
                        infixFunctionCall.line,
                        null,
                        parseInfixFunctionCallOperator(identifier, infixFunctionCall),
                        expression,
                        listOf(),
                        listOf(block)
                )
            } else {
                val arg = parseRangeExpression(argOrBlock)
                expression = KtExpressionFunction(
                        infixFunctionCall.line,
                        null,
                        identifier,
                        expression,
                        listOf(arg),
                        null
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
                    .let { KtBlock(it) }
        } else null
    }

    private fun parseInfixFunctionCallOperator(identifier: String, line: Line): Symbol {
        return when (identifier) {
            "with" -> OPERATOR_WITH
            else -> throw LineException("infix operator $identifier not supported", line)
        }
    }

    private fun parseRangeExpression(rangeExpression: AlRule): KtExpression {
        return reduceRight(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
            KtExpressionOperator(
                    rangeExpression.line,
                    null,
                    OPERATOR_RANGE,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseAdditiveExpression(additiveExpression: AlRule): KtExpression {
        return reduceOp(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
            val operator = when (op.firstAsTokenType()) {
                AlTokenType.ADD -> OPERATOR_ADD
                AlTokenType.SUB -> OPERATOR_SUB
                else -> throw LineException("additive operator expected", additiveExpression)
            }
            KtExpressionOperator(
                    additiveExpression.line,
                    null,
                    operator,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }

    private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule): KtExpression {
        return reduceOp(multiplicativeExpression, { KtExpressionParserUnary.parse(it.firstAsRule().firstAsRule()) }) { x, y, op ->
            val operator = when (op.firstAsTokenType()) {
                AlTokenType.MULT -> OPERATOR_MUL
                AlTokenType.MOD -> OPERATOR_MOD
                AlTokenType.DIV -> OPERATOR_DIV
                else -> throw LineException("multiplicative operator expected", multiplicativeExpression)
            }
            KtExpressionOperator(
                    multiplicativeExpression.line,
                    null,
                    operator,
                    x,
                    listOf(y),
                    listOf()
            )
        }
    }
}