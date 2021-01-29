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
import verikc.base.ast.AnnotationFunction
import verikc.base.ast.LineException
import verikc.base.ast.MutabilityType
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserFunction {

    fun parse(
        functionDeclaration: AlTree,
        annotations: List<AnnotationFunction>,
        symbolContext: SymbolContext
    ): KtFunction {
        val line = functionDeclaration.find(AlTerminal.FUN).line
        val identifier = functionDeclaration
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val parameterProperties = functionDeclaration
            .find(AlRule.FUNCTION_VALUE_PARAMETERS)
            .findAll(AlRule.FUNCTION_VALUE_PARAMETER)
            .map { parseFunctionValueParameter(it, symbolContext) }

        val returnTypeIdentifier = if (functionDeclaration.contains(AlRule.TYPE)) {
            KtParserTypeIdentifier.parseType(functionDeclaration.find(AlRule.TYPE))
        } else "Unit"

        val block = if (functionDeclaration.contains(AlRule.FUNCTION_BODY)) {
            val functionBody = functionDeclaration.find(AlRule.FUNCTION_BODY)
            if (!functionBody.contains(AlRule.BLOCK))
                throw LineException("function block expected", line)
            KtParserBlock.parseBlock(functionBody.find(AlRule.BLOCK), symbolContext)
        } else KtParserBlock.emptyBlock(line, symbolContext)

        val (typeFunctionExpressions, blockWithoutTypeFunctionExpressions) = splitTypeFunctionExpressions(block)

        return KtFunction(
            line,
            identifier,
            symbol,
            annotations,
            parameterProperties,
            returnTypeIdentifier,
            typeFunctionExpressions,
            blockWithoutTypeFunctionExpressions
        )
    }

    private fun parseFunctionValueParameter(functionValueParameter: AlTree, symbolContext: SymbolContext): KtProperty {
        val identifier = functionValueParameter
            .find(AlRule.PARAMETER)
            .find(AlRule.SIMPLE_IDENTIFIER)
            .unwrap().text
        val symbol = symbolContext.registerSymbol(identifier)

        val typeIdentifier = KtParserTypeIdentifier.parseType(
            functionValueParameter
                .find(AlRule.PARAMETER)
                .find(AlRule.TYPE)
        )
        val expression = functionValueParameter.findOrNull(AlRule.EXPRESSION)?.let { KtExpression(it, symbolContext) }

        return KtProperty(
            functionValueParameter.line,
            identifier,
            symbol,
            MutabilityType.VAL,
            listOf(),
            typeIdentifier,
            expression
        )
    }

    private fun splitTypeFunctionExpressions(block: KtBlock): Pair<List<KtExpressionFunction>, KtBlock> {
        val typeFunctionExpressions = ArrayList<KtExpressionFunction>()
        for (statement in block.statements) {
            if (statement is KtStatementExpression
                && statement.expression is KtExpressionFunction
                && statement.expression.identifier == "type"
            ) {
                typeFunctionExpressions.add(statement.expression)
            } else break
        }
        val statements = block.statements.drop(typeFunctionExpressions.size)
        return Pair(
            typeFunctionExpressions,
            KtBlock(block.line, block.symbol, block.lambdaProperties, statements)
        )
    }
}
