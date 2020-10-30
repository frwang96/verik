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
import verik.core.base.LineException
import verik.core.base.SymbolIndexer
import verik.core.kt.ast.*

object KtParserBlock {

    fun emptyBlock(line: Int, indexer: SymbolIndexer): KtBlock {
        return KtBlock(
                line,
                indexer.register("block"),
                listOf(),
                listOf()
        )
    }

    fun expressionBlock(expression: KtExpression, indexer: SymbolIndexer): KtBlock {
        return KtBlock(
                expression.line,
                indexer.register("block"),
                listOf(),
                listOf(KtStatementExpression(expression))
        )
    }

    fun parseBlock(block: AlRule, indexer: SymbolIndexer): KtBlock {
        val symbol = indexer.register("block")
        val statements = block
                .childAs(AlRuleType.STATEMENTS)
                .childrenAs(AlRuleType.STATEMENT)
                .map { KtStatement(it, indexer) }
        return KtBlock(
                block.line,
                symbol,
                listOf(),
                statements
        )
    }

    fun parseControlStructureBody(controlStructureBody: AlRule, indexer: SymbolIndexer): KtBlock {
        val blockOrStatement = controlStructureBody.firstAsRule()
        return when (blockOrStatement.type) {
            AlRuleType.BLOCK -> {
                parseBlock(blockOrStatement, indexer)
            }
            AlRuleType.STATEMENT -> {
                val symbol = indexer.register("block")
                KtBlock(
                        blockOrStatement.line,
                        symbol,
                        listOf(),
                        listOf(KtStatement(blockOrStatement, indexer))
                )
            }
            else -> {
                throw LineException("block or statement expected", blockOrStatement)
            }
        }
    }

    fun parseLambdaLiteral(lambdaLiteral: AlRule, indexer: SymbolIndexer): KtBlock {
        val symbol = indexer.register("block")
        val lambdaProperties = if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
            val simpleIdentifiers = lambdaLiteral
                    .childAs(AlRuleType.LAMBDA_PARAMETERS)
                    .childrenAs(AlRuleType.LAMBDA_PARAMETER)
                    .map { it.childAs(AlRuleType.VARIABLE_DECLARATION) }
                    .map { it.childAs(AlRuleType.SIMPLE_IDENTIFIER) }
            simpleIdentifiers.map {
                val identifier = it.firstAsTokenText()
                KtLambdaProperty(
                        it.line,
                        identifier,
                        indexer.register(identifier),
                        null
                )
            }
        } else listOf()

        val statements = lambdaLiteral
                .childAs(AlRuleType.STATEMENTS)
                .childrenAs(AlRuleType.STATEMENT)
                .map { KtStatement(it, indexer) }

        return KtBlock(
                lambdaLiteral.line,
                symbol,
                lambdaProperties,
                statements
        )
    }
}