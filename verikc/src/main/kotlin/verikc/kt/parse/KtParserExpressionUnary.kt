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

package verikc.kt.parse

import verikc.al.AlRule
import verikc.al.AlRuleType
import verikc.al.AlToken
import verikc.al.AlTokenType
import verikc.base.SymbolIndexer
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import verikc.kt.ast.KtExpression
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionOperator
import verikc.kt.ast.KtExpressionProperty
import verikc.lang.LangSymbol.OPERATOR_COM
import verikc.lang.LangSymbol.OPERATOR_FOREVER
import verikc.lang.LangSymbol.OPERATOR_ON
import verikc.lang.LangSymbol.OPERATOR_REPEAT
import verikc.lang.LangSymbol.OPERATOR_SEQ

object KtParserExpressionUnary {

    fun parsePrefixUnaryExpression(prefixUnaryExpression: AlRule, indexer: SymbolIndexer): KtExpression {
        return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it, indexer) }) { x, op ->
            val prefix = op.firstAsRule().first()
            val identifier = when {
                prefix is AlToken && prefix.type == AlTokenType.INCR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression.line)
                prefix is AlToken && prefix.type == AlTokenType.DECR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression.line)
                prefix is AlToken && prefix.type == AlTokenType.ADD -> "+"
                prefix is AlToken && prefix.type == AlTokenType.SUB -> "-"
                prefix is AlRule && prefix.type == AlRuleType.EXCL -> "!"
                else -> throw LineException("prefix unary operator expected", prefixUnaryExpression.line)
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

    fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule, indexer: SymbolIndexer): KtExpression {
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
                        val operator = parseLambdaOperator(identifier, postfixUnaryExpression.line)
                        val block = suffix
                                .childAs(AlRuleType.ANNOTATED_LAMBDA)
                                .childAs(AlRuleType.LAMBDA_LITERAL)
                                .let { KtParserBlock.parseLambdaLiteral(it, indexer) }
                        if (block.lambdaProperties.isNotEmpty()) {
                            throw LineException("illegal lambda parameter", postfixUnaryExpression.line)
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
                    throw LineException("illegal call receiver", postfixUnaryExpression.line)
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
                        throw LineException("postfix unary operator not supported", postfixUnaryExpression.line)
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
                    else -> throw LineException("postfix unary suffix expected", postfixUnaryExpression.line)
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

    private fun reduceLeft(
            root: AlRule,
            map: (AlRule) -> KtExpression,
            acc: (KtExpression, AlRule) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        val reversedChildren = root.children.reversed()
        var x = map(reversedChildren[0].asRule())
        for (child in reversedChildren.drop(1)) {
            x = acc(x, child.asRule())
        }
        return x
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
