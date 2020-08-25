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
import verik.core.kt.KtDeclarationLambdaProperty
import verik.core.kt.KtStatement

object KtBlockParser {

    fun parse(block: AlRule, indexer: SymbolIndexer): KtBlock {
        return when (block.type) {
            AlRuleType.BLOCK -> {
                val statements = block
                        .childAs(AlRuleType.STATEMENTS)
                        .childrenAs(AlRuleType.STATEMENT)
                        .map { KtStatement(it, indexer) }
                KtBlock(block.line, listOf(), statements)
            }
            AlRuleType.STATEMENT -> {
                KtBlock(block.line, listOf(), listOf(KtStatement(block, indexer)))
            }
            AlRuleType.LAMBDA_LITERAL -> {
                val primaryProperties = if (block.containsType(AlRuleType.LAMBDA_PARAMETERS)) {
                    val simpleIdentifiers = block
                            .childAs(AlRuleType.LAMBDA_PARAMETERS)
                            .childrenAs(AlRuleType.LAMBDA_PARAMETER)
                            .map { it.childAs(AlRuleType.VARIABLE_DECLARATION) }
                            .map { it.childAs(AlRuleType.SIMPLE_IDENTIFIER) }
                    simpleIdentifiers.map {
                        val identifier = it.firstAsTokenText()
                        KtDeclarationLambdaProperty(
                                it.line,
                                identifier,
                                indexer.register(identifier),
                                null
                        )
                    }
                } else listOf()

                val statements = block
                        .childAs(AlRuleType.STATEMENTS)
                        .childrenAs(AlRuleType.STATEMENT)
                        .map { KtStatement(it, indexer) }

                KtBlock(block.line, primaryProperties, statements)
            }
            else -> throw LineException("block or statement or lambda literal expected", block)
        }
    }
}