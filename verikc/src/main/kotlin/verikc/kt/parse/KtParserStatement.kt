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
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_DO_WHILE
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_WHILE

object KtParserStatement {

    fun parse(statement: AlxTree, symbolContext: SymbolContext): KtStatement {
        val child = statement.unwrap()
        return when (child.index) {
            AlxRuleIndex.DECLARATION -> parseDeclaration(child, symbolContext)
            AlxRuleIndex.ASSIGNMENT -> parseAssignment(child, symbolContext)
            AlxRuleIndex.LOOP_STATEMENT -> parseLoopStatement(child, symbolContext)
            AlxRuleIndex.EXPRESSION -> KtStatementExpression(KtExpression(child, symbolContext))
            else -> throw LineException("declaration or loop or expression expected", statement.line)
        }
    }

    private fun parseDeclaration(declaration: AlxTree, symbolContext: SymbolContext): KtStatementDeclaration {
        val primaryProperty = KtDeclaration(declaration, symbolContext)
        if (primaryProperty !is KtPrimaryProperty) {
            throw LineException("illegal declaration", primaryProperty.line)
        }
        return KtStatementDeclaration(primaryProperty)
    }

    private fun parseAssignment(assignment: AlxTree, symbolContext: SymbolContext): KtStatementExpression {
        val expression = KtParserExpression.parse(assignment.find(AlxRuleIndex.EXPRESSION), symbolContext)

        val function = if (assignment.contains(AlxRuleIndex.ASSIGNMENT_AND_OPERATOR)) {
            when (assignment.find(AlxRuleIndex.ASSIGNMENT_AND_OPERATOR).unwrap().index) {
                AlxTerminalIndex.ADD_ASSIGNMENT -> "+"
                AlxTerminalIndex.SUB_ASSIGNMENT -> "-"
                AlxTerminalIndex.MULT_ASSIGNMENT -> "*"
                AlxTerminalIndex.DIV_ASSIGNMENT -> "/"
                AlxTerminalIndex.MOD_ASSIGNMENT -> "%"
                else -> throw LineException("add or mult assignment expected", assignment.line)
            }
        } else null

        val assignableExpression = if (assignment.contains(AlxRuleIndex.DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            parseDirectlyAssignableExpression(
                assignment.find(AlxRuleIndex.DIRECTLY_ASSIGNABLE_EXPRESSION),
                symbolContext
            )
        } else {
            parseAssignableExpression(assignment.find(AlxRuleIndex.ASSIGNABLE_EXPRESSION), symbolContext)
        }

        return if (function != null) {
            val expressionFunction = KtExpressionFunction(
                assignment.line,
                null,
                function,
                assignableExpression,
                listOf(expression),
                null
            )
            KtStatementExpression.wrapFunction(
                assignment.line,
                null,
                "=",
                assignableExpression,
                listOf(expressionFunction),
                null
            )
        } else {
            KtStatementExpression.wrapFunction(
                assignment.line,
                null,
                "=",
                assignableExpression,
                listOf(expression),
                null
            )
        }
    }

    private fun parseDirectlyAssignableExpression(
        directlyAssignableExpression: AlxTree,
        symbolContext: SymbolContext,
    ): KtExpression {
        var directlyAssignableExpressionWalk = directlyAssignableExpression
        while (directlyAssignableExpressionWalk.contains(AlxRuleIndex.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            directlyAssignableExpressionWalk = directlyAssignableExpressionWalk
                .find(AlxRuleIndex.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)
                .find(AlxRuleIndex.DIRECTLY_ASSIGNABLE_EXPRESSION)
        }

        return if (directlyAssignableExpressionWalk.contains(AlxRuleIndex.POSTFIX_UNARY_EXPRESSION)) {
            val postfixUnaryExpression = directlyAssignableExpressionWalk.find(AlxRuleIndex.POSTFIX_UNARY_EXPRESSION)
            val expression = KtParserExpressionUnary.parsePostfixUnaryExpression(postfixUnaryExpression, symbolContext)

            val child = directlyAssignableExpressionWalk.find(AlxRuleIndex.ASSIGNABLE_SUFFIX).unwrap()
            when (child.index) {
                AlxRuleIndex.INDEXING_SUFFIX -> {
                    val args = child
                        .findAll(AlxRuleIndex.EXPRESSION)
                        .map { KtExpression(it, symbolContext) }
                    KtExpressionFunction(directlyAssignableExpression.line, null, "get", expression, args, null)
                }
                AlxRuleIndex.NAVIGATION_SUFFIX -> {
                    val identifier = child
                        .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
                        .find(AlxTerminalIndex.IDENTIFIER).text!!
                    KtExpressionProperty(directlyAssignableExpression.line, null, identifier, expression, null)
                }
                else -> throw LineException("illegal assignment suffix", child.line)
            }
        } else {
            val identifier = directlyAssignableExpressionWalk
                .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
                .find(AlxTerminalIndex.IDENTIFIER)
            KtExpressionProperty(identifier.line, null, identifier.text!!, null, null)
        }
    }

    private fun parseAssignableExpression(assignableExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        var assignableExpressionWalk = assignableExpression
        while (assignableExpressionWalk.contains(AlxRuleIndex.PARENTHESIZED_ASSIGNABLE_EXPRESSION)) {
            assignableExpressionWalk = assignableExpressionWalk
                .find(AlxRuleIndex.PARENTHESIZED_ASSIGNABLE_EXPRESSION)
                .find(AlxRuleIndex.ASSIGNABLE_EXPRESSION)
        }
        return KtParserExpressionUnary.parsePrefixUnaryExpression(
            assignableExpressionWalk.find(AlxRuleIndex.PREFIX_UNARY_EXPRESSION),
            symbolContext
        )
    }

    private fun parseLoopStatement(loopStatement: AlxTree, symbolContext: SymbolContext): KtStatementExpression {
        val child = loopStatement.unwrap()
        val expression = KtExpression(child.find(AlxRuleIndex.EXPRESSION), symbolContext)
        val block = if (child.contains(AlxRuleIndex.CONTROL_STRUCTURE_BODY)) {
            KtParserBlock.parseControlStructureBody(
                child.find(AlxRuleIndex.CONTROL_STRUCTURE_BODY),
                symbolContext
            )
        } else KtParserBlock.emptyBlock(child.line, symbolContext)

        return when (child.index) {
            AlxRuleIndex.FOR_STATEMENT -> {
                val identifier = child
                    .find(AlxRuleIndex.VARIABLE_DECLARATION)
                    .find(AlxRuleIndex.SIMPLE_IDENTIFIER)
                    .find(AlxTerminalIndex.IDENTIFIER).text!!
                val lambdaProperty = KtLambdaProperty(
                    child.line,
                    identifier,
                    symbolContext.registerSymbol(identifier),
                    null
                )
                KtStatementExpression(
                    KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_FOR_EACH,
                        null,
                        listOf(expression),
                        listOf(
                            KtBlock(
                                block.line,
                                block.symbol,
                                listOf(lambdaProperty),
                                block.statements
                            )
                        )
                    )
                )
            }
            AlxRuleIndex.WHILE_STATEMENT -> {
                KtStatementExpression(
                    KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_WHILE,
                        null,
                        listOf(expression),
                        listOf(block)
                    )
                )
            }
            AlxRuleIndex.DO_WHILE_STATEMENT -> {
                KtStatementExpression(
                    KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_DO_WHILE,
                        null,
                        listOf(expression),
                        listOf(block)
                    )
                )
            }
            else -> throw LineException("loop statement expected", loopStatement.line)
        }
    }
}
