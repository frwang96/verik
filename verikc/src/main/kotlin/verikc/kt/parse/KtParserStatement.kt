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

import verikc.al.*
import verikc.base.*
import verikc.base.ast.*
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_DO_WHILE
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_WHILE

object KtParserStatement {

    fun parse(statement: AlRule, indexer: SymbolIndexer): KtStatement {
        val child = statement.firstAsRule()
        return when (child.type) {
            AlRuleType.DECLARATION -> parseDeclaration(child, indexer)
            AlRuleType.ASSIGNMENT -> parseAssignment(child, indexer)
            AlRuleType.LOOP_STATEMENT -> parseLoopStatement(child, indexer)
            AlRuleType.EXPRESSION -> KtStatementExpression(KtExpression(child, indexer))
            else -> throw LineException("declaration or loop or expression expected", statement)
        }
    }

    private fun parseDeclaration(declaration: AlRule, indexer: SymbolIndexer): KtStatementDeclaration {
        val primaryProperty = KtDeclaration(declaration, indexer)
        if (primaryProperty !is KtPrimaryProperty) {
            throw LineException("illegal declaration", primaryProperty)
        }
        return KtStatementDeclaration(primaryProperty)
    }

    private fun parseAssignment(assignment: AlRule, indexer: SymbolIndexer): KtStatementExpression {
        val expression = KtParserExpression.parse(assignment.childAs(AlRuleType.EXPRESSION), indexer)

        val function = if (assignment.containsType(AlRuleType.ASSIGNMENT_AND_OPERATOR)) {
            when (assignment.childAs(AlRuleType.ASSIGNMENT_AND_OPERATOR).firstAsTokenType()) {
                AlTokenType.ADD_ASSIGNMENT -> "+"
                AlTokenType.SUB_ASSIGNMENT -> "-"
                AlTokenType.MULT_ASSIGNMENT -> "*"
                AlTokenType.DIV_ASSIGNMENT -> "/"
                AlTokenType.MOD_ASSIGNMENT -> "%"
                else -> throw LineException("add or mult assignment expected", assignment)
            }
        } else null

        val assignableExpression = if (assignment.containsType(AlRuleType.DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            parseDirectlyAssignableExpression(assignment.childAs(AlRuleType.DIRECTLY_ASSIGNABLE_EXPRESSION), indexer)
        } else {
            parseAssignableExpression(assignment.childAs(AlRuleType.ASSIGNABLE_EXPRESSION), indexer)
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
            directlyAssignableExpression: AlRule,
            indexer: SymbolIndexer,
    ): KtExpression {
        var directlyAssignableExpressionWalk = directlyAssignableExpression
        while (directlyAssignableExpressionWalk.containsType(AlRuleType.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)) {
            directlyAssignableExpressionWalk = directlyAssignableExpressionWalk
                    .childAs(AlRuleType.PARENTHESIZED_DIRECTLY_ASSIGNABLE_EXPRESSION)
                    .childAs(AlRuleType.DIRECTLY_ASSIGNABLE_EXPRESSION)
        }

        return if (directlyAssignableExpressionWalk.containsType(AlRuleType.POSTFIX_UNARY_EXPRESSION)) {
            val postfixUnaryExpression = directlyAssignableExpressionWalk.childAs(AlRuleType.POSTFIX_UNARY_EXPRESSION)
            val expression = KtParserExpressionUnary.parsePostfixUnaryExpression(postfixUnaryExpression, indexer)

            val assignableSuffix = directlyAssignableExpressionWalk.childAs(AlRuleType.ASSIGNABLE_SUFFIX)
            when (assignableSuffix.firstAsRuleType()) {
                AlRuleType.INDEXING_SUFFIX -> {
                    val args = assignableSuffix
                            .firstAsRule()
                            .childrenAs(AlRuleType.EXPRESSION)
                            .map { KtExpression(it, indexer) }
                    KtExpressionFunction(directlyAssignableExpression.line, null, "get", expression, args, null)
                }
                AlRuleType.NAVIGATION_SUFFIX -> {
                    val identifier = assignableSuffix
                            .firstAsRule()
                            .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                            .firstAsTokenText()
                    KtExpressionProperty(directlyAssignableExpression.line, null, identifier, expression, null)
                }
                else -> throw LineException("illegal assignment suffix", assignableSuffix)
            }
        } else {
            val simpleIdentifier = directlyAssignableExpressionWalk.childAs(AlRuleType.SIMPLE_IDENTIFIER)
            KtExpressionProperty(simpleIdentifier.line, null, simpleIdentifier.firstAsTokenText(), null, null)
        }
    }

    private fun parseAssignableExpression(assignableExpression: AlRule, indexer: SymbolIndexer): KtExpression {
        var assignableExpressionWalk = assignableExpression
        while (assignableExpressionWalk.containsType(AlRuleType.PARENTHESIZED_ASSIGNABLE_EXPRESSION)) {
            assignableExpressionWalk = assignableExpressionWalk
                    .childAs(AlRuleType.PARENTHESIZED_ASSIGNABLE_EXPRESSION)
                    .childAs(AlRuleType.ASSIGNABLE_EXPRESSION)
        }
        return KtParserExpressionUnary.parsePrefixUnaryExpression(
                assignableExpressionWalk.childAs(AlRuleType.PREFIX_UNARY_EXPRESSION),
                indexer
        )
    }

    private fun parseLoopStatement(loopStatement: AlRule, indexer: SymbolIndexer): KtStatementExpression {
        val child = loopStatement.firstAsRule()
        val expression = KtExpression(child.childAs(AlRuleType.EXPRESSION), indexer)
        val block = if (child.containsType(AlRuleType.CONTROL_STRUCTURE_BODY)) {
            KtParserBlock.parseControlStructureBody(
                    child.childAs(AlRuleType.CONTROL_STRUCTURE_BODY),
                    indexer
            )
        } else KtParserBlock.emptyBlock(child.line, indexer)

        return when (child.type) {
            AlRuleType.FOR_STATEMENT -> {
                val identifier = child
                        .childAs(AlRuleType.VARIABLE_DECLARATION)
                        .childAs(AlRuleType.SIMPLE_IDENTIFIER)
                        .firstAsTokenText()
                val lambdaProperty = KtLambdaProperty(
                        child.line,
                        identifier,
                        indexer.register(identifier),
                        null
                )
                KtStatementExpression(KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_FOR_EACH,
                        null,
                        listOf(expression),
                        listOf(KtBlock(
                                block.line,
                                block.symbol,
                                listOf(lambdaProperty),
                                block.statements
                        ))
                ))
            }
            AlRuleType.WHILE_STATEMENT -> {
                KtStatementExpression(KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_WHILE,
                        null,
                        listOf(expression),
                        listOf(block)
                ))
            }
            AlRuleType.DO_WHILE_STATEMENT -> {
                KtStatementExpression(KtExpressionOperator(
                        child.line,
                        null,
                        OPERATOR_DO_WHILE,
                        null,
                        listOf(expression),
                        listOf(block)
                ))
            }
            else -> throw LineException("loop statement expected", loopStatement)
        }
    }
}
