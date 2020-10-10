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
import verik.core.base.LineException
import verik.core.base.SymbolIndexer
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_DO_WHILE
import verik.core.lang.LangSymbol.OPERATOR_FOR_EACH
import verik.core.lang.LangSymbol.OPERATOR_WHILE

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
        val identifier = when (assignment.childAs(AlRuleType.ASSIGNMENT_AND_OPERATOR).firstAsTokenType()) {
            AlTokenType.ADD_ASSIGNMENT -> "+="
            AlTokenType.MULT_ASSIGNMENT -> "*="
            else -> throw LineException("add or mult assignment expected", assignment)
        }
        var assignableExpression = assignment.childAs(AlRuleType.ASSIGNABLE_EXPRESSION)
        while (assignableExpression.containsType(AlRuleType.PARENTHESIZED_ASSIGNABLE_EXPRESSION)) {
            assignableExpression = assignableExpression
                    .childAs(AlRuleType.PARENTHESIZED_ASSIGNABLE_EXPRESSION)
                    .childAs(AlRuleType.ASSIGNABLE_EXPRESSION)
        }
        val prefixUnaryExpression = KtParserExpressionUnary.parse(
                assignableExpression.childAs(AlRuleType.PREFIX_UNARY_EXPRESSION),
                indexer
        )
        return KtStatementExpression.wrapFunction(
                assignment.line,
                null,
                identifier,
                prefixUnaryExpression,
                listOf(expression),
                null
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