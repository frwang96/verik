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

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
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

    fun parsePrefixUnaryExpression(prefixUnaryExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it, symbolContext) }) { x, op ->
            val identifier = when (op.find(AlxRuleIndex.PREFIX_UNARY_OPERATOR).unwrap().index) {
                AlxTerminalIndex.INCR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression.line)
                AlxTerminalIndex.DECR ->
                    throw LineException("postfix unary operator not supported", prefixUnaryExpression.line)
                AlxTerminalIndex.ADD -> "+"
                AlxTerminalIndex.SUB -> "-"
                AlxRuleIndex.EXCL -> "!"
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

    fun parsePostfixUnaryExpression(postfixUnaryExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        val primaryExpression = KtParserExpressionPrimary.parse(
            postfixUnaryExpression.find(AlxRuleIndex.PRIMARY_EXPRESSION),
            symbolContext
        )
        var expression: KtExpression? = null
        var identifier: String? = null
        if (primaryExpression is KtExpressionProperty && primaryExpression.receiver == null) {
            identifier = primaryExpression.identifier
        } else {
            expression = primaryExpression
        }
        val suffixes = postfixUnaryExpression
            .findAll(AlxRuleIndex.POSTFIX_UNARY_SUFFIX)
            .map { it.unwrap() }
        for (suffix in suffixes) {
            if (suffix.index == AlxRuleIndex.CALL_SUFFIX) {
                if (identifier != null) {
                    val args = suffix
                        .findAll(AlxRuleIndex.VALUE_ARGUMENTS)
                        .flatMap { it.findAll(AlxRuleIndex.VALUE_ARGUMENT) }
                        .map { it.find(AlxRuleIndex.EXPRESSION) }
                        .map { KtExpression(it, symbolContext) }
                    if (suffix.contains(AlxRuleIndex.ANNOTATED_LAMBDA)) {
                        val operator = parseLambdaOperator(identifier, postfixUnaryExpression.line)
                        val block = suffix
                            .find(AlxRuleIndex.ANNOTATED_LAMBDA)
                            .find(AlxRuleIndex.LAMBDA_LITERAL)
                            .let { KtParserBlock.parseLambdaLiteral(it, symbolContext) }
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
                when (suffix.index) {
                    AlxRuleIndex.POSTFIX_UNARY_OPERATOR -> {
                        throw LineException("postfix unary operator not supported", postfixUnaryExpression.line)
                    }
                    AlxRuleIndex.INDEXING_SUFFIX -> {
                        val args = suffix
                            .findAll(AlxRuleIndex.EXPRESSION)
                            .map { KtExpression(it, symbolContext) }
                        expression = KtExpressionFunction(
                            postfixUnaryExpression.line,
                            null,
                            "get",
                            expression,
                            args,
                            null
                        )
                    }
                    AlxRuleIndex.NAVIGATION_SUFFIX -> {
                        identifier = suffix
                            .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
                            .find(AlxTerminalIndex.IDENTIFIER).text!!
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
        root: AlxTree,
        map: (AlxTree) -> KtExpression,
        acc: (KtExpression, AlxTree) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        val reversedChildren = root.children.reversed()
        var x = map(reversedChildren[0])
        for (child in reversedChildren.drop(1)) {
            x = acc(x, child)
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
