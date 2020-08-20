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
import verik.core.base.LineException
import verik.core.kt.*

object KtExpressionParserUnary {

    fun parse(prefixUnaryExpression: AlRule): KtExpression {
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
                    null,
                    identifier,
                    x,
                    listOf(),
                    listOf()
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

    private fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule): KtExpression {
        val primaryExpression = KtExpressionParserPrimary.parse(postfixUnaryExpression.childAs(AlRuleType.PRIMARY_EXPRESSION))
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
                                .let { KtBlock(it) }
                        expression = KtExpressionOperator(
                                postfixUnaryExpression.line,
                                null,
                                operatorIdentifier,
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
                    throw LineException("illegal call target", postfixUnaryExpression)
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
                    AlRuleType.INDEXING_SUFFIX -> {
                        val args = suffix.childrenAs(AlRuleType.EXPRESSION).map { KtExpression(it) }
                        expression = KtExpressionOperator(
                                postfixUnaryExpression.line,
                                null,
                                KtOperatorIdentifier.GET,
                                expression,
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
                    null,
                    identifier,
                    expression,
                    null
            )
        }
        return expression!!
    }
}