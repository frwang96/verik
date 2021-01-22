/*
 * Copyright (c) 2020 Francis Wang
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

import verikc.al.ast.AlRule
import verikc.al.ast.AlTerminal
import verikc.al.ast.AlTree
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

    fun parsePrefixUnaryExpression(prefixUnaryExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it, symbolContext) }) { x, op ->
            val identifier = when (op.find(AlRule.PREFIX_UNARY_OPERATOR).unwrap().index) {
                AlTerminal.INCR -> "++_"
                AlTerminal.DECR -> "--_"
                AlTerminal.ADD -> "+"
                AlTerminal.SUB -> "-"
                AlRule.EXCL -> "!"
                else -> throw LineException("prefix unary operator expected", prefixUnaryExpression.line)
            }
            KtExpressionFunction(prefixUnaryExpression.line, identifier, x, null, listOf())
        }
    }

    fun parsePostfixUnaryExpression(postfixUnaryExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val primaryExpression = KtParserExpressionPrimary.parse(
            postfixUnaryExpression.find(AlRule.PRIMARY_EXPRESSION),
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
            .findAll(AlRule.POSTFIX_UNARY_SUFFIX)
            .map { it.unwrap() }
        for (suffix in suffixes) {
            if (suffix.index == AlRule.CALL_SUFFIX) {
                if (identifier != null) {
                    expression = parseCallSuffix(suffix, identifier, expression, symbolContext)
                    identifier = null
                } else {
                    throw LineException("illegal call receiver", postfixUnaryExpression.line)
                }
            } else {
                if (identifier != null) {
                    expression = KtExpressionProperty(postfixUnaryExpression.line, identifier, expression)
                    identifier = null
                }
                when (suffix.index) {
                    AlRule.POSTFIX_UNARY_OPERATOR -> {
                        val functionIdentifier = when (suffix.unwrap().index) {
                            AlTerminal.INCR -> "_++"
                            AlTerminal.DECR -> "_--"
                            else -> throw LineException("postfix unary operator expected", postfixUnaryExpression.line)
                        }
                        expression = KtExpressionFunction(
                            postfixUnaryExpression.line,
                            functionIdentifier,
                            expression,
                            null,
                            listOf()
                        )
                    }
                    AlRule.INDEXING_SUFFIX -> {
                        val args = suffix
                            .findAll(AlRule.EXPRESSION)
                            .map { KtExpression(it, symbolContext) }
                        expression = KtExpressionFunction(
                            postfixUnaryExpression.line,
                            "get",
                            expression,
                            null,
                            args
                        )
                    }
                    AlRule.NAVIGATION_SUFFIX -> {
                        identifier = suffix
                            .find(AlRule.SIMPLE_IDENTIFIER)
                            .unwrap().text
                    }
                    else -> throw LineException("postfix unary suffix expected", postfixUnaryExpression.line)
                }
            }
        }
        if (identifier != null) {
            expression = KtExpressionProperty(postfixUnaryExpression.line, identifier, expression)
        }
        return expression!!
    }

    private fun reduceLeft(
        root: AlTree,
        map: (AlTree) -> KtExpression,
        acc: (KtExpression, AlTree) -> KtExpression
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

    private fun parseCallSuffix(
        callSuffix: AlTree,
        identifier: String,
        expression: KtExpression?,
        symbolContext: SymbolContext
    ): KtExpression {
        val valueArguments = callSuffix
            .findAll(AlRule.VALUE_ARGUMENTS)
            .flatMap { it.findAll(AlRule.VALUE_ARGUMENT) }
        val args = valueArguments
            .map { it.find(AlRule.EXPRESSION) }
            .map { KtExpression(it, symbolContext) }
        val argIdentifiers = if (valueArguments.any { it.contains(AlRule.SIMPLE_IDENTIFIER) }) {
            valueArguments.map {
                if (it.contains(AlRule.SIMPLE_IDENTIFIER)) it.find(AlRule.SIMPLE_IDENTIFIER).unwrap().text
                else throw LineException("either all or none of the arguments should be named", callSuffix.line)
            }
        } else null

        return if (callSuffix.contains(AlRule.ANNOTATED_LAMBDA)) {
            val operator = parseLambdaOperator(identifier, callSuffix.line)
            val block = callSuffix
                .find(AlRule.ANNOTATED_LAMBDA)
                .find(AlRule.LAMBDA_LITERAL)
                .let { KtParserBlock.parseLambdaLiteral(it, symbolContext) }
            if (block.lambdaProperties.isNotEmpty())
                throw LineException("illegal lambda parameter", callSuffix.line)
            if (argIdentifiers != null)
                throw LineException("named arguments not permitted here", callSuffix.line)
            KtExpressionOperator(
                callSuffix.line,
                operator,
                expression,
                args,
                listOf(block)
            )
        } else {
            KtExpressionFunction(
                callSuffix.line,
                identifier,
                expression,
                argIdentifiers,
                args
            )
        }
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
