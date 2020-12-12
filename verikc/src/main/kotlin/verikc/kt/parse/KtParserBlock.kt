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

import verikc.al.AlRule
import verikc.al.AlRuleType
import verikc.base.SymbolContext
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.kt.ast.*

object KtParserBlock {

    fun emptyBlock(line: Line, symbolContext: SymbolContext): KtBlock {
        return KtBlock(line, symbolContext.registerSymbol("block"), listOf(), listOf())
    }

    fun expressionBlock(expression: KtExpression, symbolContext: SymbolContext): KtBlock {
        return KtBlock(
            expression.line,
            symbolContext.registerSymbol("block"),
            listOf(),
            listOf(KtStatementExpression(expression))
        )
    }

    fun parseBlock(block: AlRule, symbolContext: SymbolContext): KtBlock {
        val symbol = symbolContext.registerSymbol("block")
        val statements = block
            .childAs(AlRuleType.STATEMENTS)
            .childrenAs(AlRuleType.STATEMENT)
            .map { KtStatement(it, symbolContext) }
        return KtBlock(block.line, symbol, listOf(), statements)
    }

    fun parseControlStructureBody(controlStructureBody: AlRule, symbolContext: SymbolContext): KtBlock {
        val blockOrStatement = controlStructureBody.firstAsRule()
        return when (blockOrStatement.type) {
            AlRuleType.BLOCK -> {
                parseBlock(blockOrStatement, symbolContext)
            }
            AlRuleType.STATEMENT -> {
                val symbol = symbolContext.registerSymbol("block")
                KtBlock(
                    blockOrStatement.line,
                    symbol,
                    listOf(),
                    listOf(KtStatement(blockOrStatement, symbolContext))
                )
            }
            else -> {
                throw LineException("block or statement expected", blockOrStatement.line)
            }
        }
    }

    fun parseLambdaLiteral(lambdaLiteral: AlRule, symbolContext: SymbolContext): KtBlock {
        val symbol = symbolContext.registerSymbol("block")
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
                    symbolContext.registerSymbol(identifier),
                    null
                )
            }
        } else listOf()

        val statements = lambdaLiteral
            .childAs(AlRuleType.STATEMENTS)
            .childrenAs(AlRuleType.STATEMENT)
            .map { KtStatement(it, symbolContext) }

        return KtBlock(lambdaLiteral.line, symbol, lambdaProperties, statements)
    }
}
