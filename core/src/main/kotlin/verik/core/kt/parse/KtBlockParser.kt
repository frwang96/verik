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
import verik.core.kt.KtBlock
import verik.core.kt.KtDeclarationLambdaParameter
import verik.core.kt.KtStatement

object KtBlockParser {

    fun parseBlock(block: AlRule, indexer: SymbolIndexer): KtBlock {
        val statements = block
                .childAs(AlRuleType.STATEMENTS)
                .childrenAs(AlRuleType.STATEMENT)
                .map { KtStatement(it, indexer) }
        return KtBlock(block.line, listOf(), statements)
    }

    fun parseControlStructureBody(controlStructureBody: AlRule, indexer: SymbolIndexer): KtBlock {
        val blockOrStatement = controlStructureBody.firstAsRule()
        return when (blockOrStatement.type) {
            AlRuleType.BLOCK -> {
                parseBlock(blockOrStatement, indexer)
            }
            AlRuleType.STATEMENT -> {
                KtBlock(
                        blockOrStatement.line,
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
        val lambdaParameters = if (lambdaLiteral.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
            val simpleIdentifiers = lambdaLiteral
                    .childAs(AlRuleType.LAMBDA_PARAMETERS)
                    .childrenAs(AlRuleType.LAMBDA_PARAMETER)
                    .map { it.childAs(AlRuleType.VARIABLE_DECLARATION) }
                    .map { it.childAs(AlRuleType.SIMPLE_IDENTIFIER) }
            simpleIdentifiers.map {
                val identifier = it.firstAsTokenText()
                KtDeclarationLambdaParameter(
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

        return KtBlock(lambdaLiteral.line, lambdaParameters, statements)
    }
}