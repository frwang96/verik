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
import verikc.base.symbol.SymbolContext
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

    fun parseBlock(block: AlxTree, symbolContext: SymbolContext): KtBlock {
        val symbol = symbolContext.registerSymbol("block")
        val statements = block
            .find(AlxRuleIndex.STATEMENTS)
            .findAll(AlxRuleIndex.STATEMENT)
            .map { KtStatement(it, symbolContext) }
        return KtBlock(block.line, symbol, listOf(), statements)
    }

    fun parseControlStructureBody(controlStructureBody: AlxTree, symbolContext: SymbolContext): KtBlock {
        val blockOrStatement = controlStructureBody.unwrap()
        return when (blockOrStatement.index) {
            AlxRuleIndex.BLOCK -> {
                parseBlock(blockOrStatement, symbolContext)
            }
            AlxRuleIndex.STATEMENT -> {
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

    fun parseLambdaLiteral(lambdaLiteral: AlxTree, symbolContext: SymbolContext): KtBlock {
        val symbol = symbolContext.registerSymbol("block")
        val lambdaProperties = if (lambdaLiteral.contains(AlxRuleIndex.LAMBDA_PARAMETERS)) {
            val identifiers = lambdaLiteral
                .find(AlxRuleIndex.LAMBDA_PARAMETERS)
                .findAll(AlxRuleIndex.LAMBDA_PARAMETER)
                .map { it.find(AlxRuleIndex.VARIABLE_DECLARATION) }
                .map { it.find(AlxRuleIndex.SIMPLE_IDENTIFIER) }
                .map { it.find(AlxTerminalIndex.IDENTIFIER) }
            identifiers.map {
                KtLambdaProperty(
                    it.line,
                    it.text!!,
                    symbolContext.registerSymbol(it.text),
                    null
                )
            }
        } else listOf()

        val statements = lambdaLiteral
            .find(AlxRuleIndex.STATEMENTS)
            .findAll(AlxRuleIndex.STATEMENT)
            .map { KtStatement(it, symbolContext) }

        return KtBlock(lambdaLiteral.line, symbol, lambdaProperties, statements)
    }
}
