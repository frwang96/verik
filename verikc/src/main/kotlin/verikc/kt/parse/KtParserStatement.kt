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
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_DO_WHILE
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_WHILE

object KtParserStatement {

    fun parse(statement: AlTree, symbolContext: SymbolContext): KtStatement {
        val child = statement.unwrap()
        return when (child.index) {
            AlRule.DECLARATION -> parseDeclaration(child, symbolContext)
            AlRule.ASSIGNMENT -> parseAssignment(child, symbolContext)
            AlRule.LOOP_STATEMENT -> parseLoopStatement(child, symbolContext)
            AlRule.EXPRESSION -> KtStatementExpression(KtExpression(child, symbolContext))
            else -> throw LineException("declaration or loop or expression expected", statement.line)
        }
    }

    private fun parseDeclaration(declaration: AlTree, symbolContext: SymbolContext): KtStatementDeclaration {
        val property = KtParserDeclaration.parse(declaration, symbolContext)
        if (property !is KtProperty) {
            throw LineException("illegal declaration", property.line)
        }
        return KtStatementDeclaration(property)
    }

    private fun parseAssignment(assignment: AlTree, symbolContext: SymbolContext): KtStatementExpression {
        val expression = KtParserExpressionBase.parse(assignment.find(AlRule.EXPRESSION), symbolContext)

        val function = if (assignment.contains(AlRule.ASSIGNMENT_AND_OPERATOR)) {
            when (assignment.find(AlRule.ASSIGNMENT_AND_OPERATOR).unwrap().index) {
                AlTerminal.ADD_ASSIGNMENT -> "+"
                AlTerminal.SUB_ASSIGNMENT -> "-"
                AlTerminal.MULT_ASSIGNMENT -> "*"
                AlTerminal.DIV_ASSIGNMENT -> "/"
                AlTerminal.MOD_ASSIGNMENT -> "%"
                else -> throw LineException("add or mult assignment expected", assignment.line)
            }
        } else null

        val assignableExpression = if (assignment.contains(AlRule.DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            parseDirectlyAssignableExpression(
                assignment.find(AlRule.DIRECTLY_ASSIGNABLE_EXPRESSION),
                symbolContext
            )
        } else {
            parseAssignableExpression(assignment.find(AlRule.ASSIGNABLE_EXPRESSION), symbolContext)
        }

        return if (function != null) {
            val expressionFunction = KtExpressionFunction(
                assignment.line,
                function,
                assignableExpression,
                listOf(expression)
            )
            KtStatementExpression.wrapFunction(assignment.line, "=", assignableExpression, listOf(expressionFunction))
        } else {
            KtStatementExpression.wrapFunction(assignment.line, "=", assignableExpression, listOf(expression))
        }
    }

    private fun parseDirectlyAssignableExpression(
        directlyAssignableExpression: AlTree,
        symbolContext: SymbolContext,
    ): KtExpression {
        var directlyAssignableExpressionWalk = directlyAssignableExpression
        while (directlyAssignableExpressionWalk.contains(AlRule.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            directlyAssignableExpressionWalk = directlyAssignableExpressionWalk
                .find(AlRule.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)
                .find(AlRule.DIRECTLY_ASSIGNABLE_EXPRESSION)
        }

        return if (directlyAssignableExpressionWalk.contains(AlRule.POSTFIX_UNARY_EXPRESSION)) {
            val postfixUnaryExpression = directlyAssignableExpressionWalk.find(AlRule.POSTFIX_UNARY_EXPRESSION)
            val expression = KtParserExpressionUnary.parsePostfixUnaryExpression(postfixUnaryExpression, symbolContext)

            val child = directlyAssignableExpressionWalk.find(AlRule.ASSIGNABLE_SUFFIX).unwrap()
            when (child.index) {
                AlRule.INDEXING_SUFFIX -> {
                    val args = child
                        .findAll(AlRule.EXPRESSION)
                        .map { KtExpression(it, symbolContext) }
                    KtExpressionFunction(directlyAssignableExpression.line, "get", expression, args)
                }
                AlRule.NAVIGATION_SUFFIX -> {
                    val identifier = child
                        .find(AlRule.SIMPLE_IDENTIFIER)
                        .unwrap().text
                    KtExpressionProperty(directlyAssignableExpression.line, identifier, expression)
                }
                else -> throw LineException("illegal assignment suffix", child.line)
            }
        } else {
            val identifier = directlyAssignableExpressionWalk.find(AlRule.SIMPLE_IDENTIFIER)
            KtExpressionProperty(identifier.line, identifier.unwrap().text, null)
        }
    }

    private fun parseAssignableExpression(assignableExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        var assignableExpressionWalk = assignableExpression
        while (assignableExpressionWalk.contains(AlRule.PARENTHESIZED_ASSIGNABLE_EXPRESSION)) {
            assignableExpressionWalk = assignableExpressionWalk
                .find(AlRule.PARENTHESIZED_ASSIGNABLE_EXPRESSION)
                .find(AlRule.ASSIGNABLE_EXPRESSION)
        }
        return KtParserExpressionUnary.parsePrefixUnaryExpression(
            assignableExpressionWalk.find(AlRule.PREFIX_UNARY_EXPRESSION),
            symbolContext
        )
    }

    private fun parseLoopStatement(loopStatement: AlTree, symbolContext: SymbolContext): KtStatementExpression {
        val child = loopStatement.unwrap()
        val expression = KtExpression(child.find(AlRule.EXPRESSION), symbolContext)
        val block = if (child.contains(AlRule.CONTROL_STRUCTURE_BODY)) {
            KtParserBlock.parseControlStructureBody(
                child.find(AlRule.CONTROL_STRUCTURE_BODY),
                symbolContext
            )
        } else KtParserBlock.emptyBlock(child.line, symbolContext)

        return when (child.index) {
            AlRule.FOR_STATEMENT -> {
                val identifier = child
                    .find(AlRule.VARIABLE_DECLARATION)
                    .find(AlRule.SIMPLE_IDENTIFIER)
                    .unwrap().text
                val lambdaProperty = KtProperty(
                    child.line,
                    identifier,
                    symbolContext.registerSymbol(identifier),
                    listOf(),
                    null,
                    null
                )
                KtStatementExpression(
                    KtExpressionOperator(
                        child.line,
                        OPERATOR_FOR_EACH,
                        null,
                        listOf(expression),
                        listOf(KtBlock(block.line, block.symbol, listOf(lambdaProperty), block.statements))
                    )
                )
            }
            AlRule.WHILE_STATEMENT -> {
                KtStatementExpression(
                    KtExpressionOperator(child.line, OPERATOR_WHILE, null, listOf(expression), listOf(block))
                )
            }
            AlRule.DO_WHILE_STATEMENT -> {
                KtStatementExpression(
                    KtExpressionOperator(
                        child.line,
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
