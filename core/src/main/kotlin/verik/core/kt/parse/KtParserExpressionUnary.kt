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
import verik.core.al.AlToken
import verik.core.al.AlTokenType
import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.base.SymbolIndexer
import verik.core.kt.KtExpression
import verik.core.kt.KtExpressionFunction
import verik.core.kt.KtExpressionOperator
import verik.core.kt.KtExpressionProperty
import verik.core.lang.LangSymbol.OPERATOR_COM
import verik.core.lang.LangSymbol.OPERATOR_FOREVER
import verik.core.lang.LangSymbol.OPERATOR_ON
import verik.core.lang.LangSymbol.OPERATOR_REPEAT
import verik.core.lang.LangSymbol.OPERATOR_SEQ

object KtParserExpressionUnary {

    fun parse(prefixUnaryExpression: AlRule, indexer: SymbolIndexer): KtExpression {
        return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it, indexer) }) { x, op ->
            val prefix = op.firstAsRule().first()
            val identifier = when {
                prefix is AlToken && prefix.type == AlTokenType.INCR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression)
                prefix is AlToken && prefix.type == AlTokenType.DECR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression)
                prefix is AlToken && prefix.type == AlTokenType.ADD -> "+"
                prefix is AlToken && prefix.type == AlTokenType.SUB -> "-"
                prefix is AlRule && prefix.type == AlRuleType.EXCL -> "!"
                else -> throw LineException("prefix unary operator expected", prefixUnaryExpression)
            }
            KtExpressionFunction(
                    prefixUnaryExpression.line,
                    null,
                    identifier,
                    x,
                    listOf(),
                    null
            )
        }
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

    private fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule, indexer: SymbolIndexer): KtExpression {
        val primaryExpression = KtParserExpressionPrimary.parse(
                postfixUnaryExpression.childAs(AlRuleType.PRIMARY_EXPRESSION),
                indexer
        )
        var expression: KtExpression? = null
        var identifier: String? = null
        if (primaryExpression is KtExpressionProperty && primaryExpression.receiver == null) {
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
                            .map { KtExpression(it, indexer) }
                    if (suffix.containsType(AlRuleType.ANNOTATED_LAMBDA)) {
                        val operator = parseLambdaOperator(identifier, postfixUnaryExpression)
                        val block = suffix
                                .childAs(AlRuleType.ANNOTATED_LAMBDA)
                                .childAs(AlRuleType.LAMBDA_LITERAL)
                                .let { KtParserBlock.parseLambdaLiteral(it, indexer) }
                        if (block.lambdaParameters.isNotEmpty()) {
                            throw LineException("illegal lambda parameter", postfixUnaryExpression)
                        }
                        expression = KtExpressionOperator(
                                postfixUnaryExpression.line,
                                null,
                                operator,
                                expression,
                                args,
                                listOf(block)
                        )
                    } else {
                        expression = KtExpressionFunction(
                                postfixUnaryExpression.line,
                                null,
                                identifier,
                                expression,
                                args,
                                null
                        )
                    }
                    identifier = null
                } else {
                    throw LineException("illegal call receiver", postfixUnaryExpression)
                }
            } else {
                if (identifier != null) {
                    expression = KtExpressionProperty(
                            postfixUnaryExpression.line,
                            null,
                            identifier,
                            expression,
                            null
                    )
                    identifier = null
                }
                when (suffix.type) {
                    AlRuleType.POSTFIX_UNARY_OPERATOR -> {
                        throw LineException("postfix unary operator not supported", postfixUnaryExpression)
                    }
                    AlRuleType.INDEXING_SUFFIX -> {
                        val args = suffix
                                .childrenAs(AlRuleType.EXPRESSION)
                                .map { KtExpression(it, indexer) }
                        expression = KtExpressionFunction(
                                postfixUnaryExpression.line,
                                null,
                                "get",
                                expression,
                                args,
                                null
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
                    null,
                    identifier,
                    expression,
                    null
            )
        }
        return expression!!
    }

    private fun parseLambdaOperator(identifier: String, line: Line): Symbol {
        return when (identifier) {
            "on" -> OPERATOR_ON
            "com" -> OPERATOR_COM
            "seq" -> OPERATOR_SEQ
            "forever" -> OPERATOR_FOREVER
            "repeat" -> OPERATOR_REPEAT
            else -> throw LineException("lambda operator $identifier not supported", line)
        }
    }
}