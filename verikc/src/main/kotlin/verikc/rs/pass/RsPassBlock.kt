/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.pass

import verikc.base.ast.ExpressionClass
import verikc.base.ast.LineException
import verikc.rs.ast.RsBlock
import verikc.rs.ast.RsStatementDeclaration
import verikc.rs.ast.RsStatementExpression
import verikc.rs.resolve.RsEvaluatorExpression
import verikc.rs.table.RsSymbolTable

object RsPassBlock {

    fun pass(block: RsBlock, symbolTable: RsSymbolTable) {
        block.lambdaProperties.forEach {
            symbolTable.setProperty(it)
        }
        block.statements.forEach {
            when (it) {
                is RsStatementDeclaration -> {
                    val expression = it.property.getExpressionNotNull()
                    RsPassExpression.pass(expression, block.symbol, symbolTable)
                    if (expression.getExpressionClassNotNull() == ExpressionClass.TYPE)
                        throw LineException("type expression not permitted", it.line)
                    it.property.typeGenerified = expression.getTypeGenerifiedNotNull()
                    it.property.evaluateResult = RsEvaluatorExpression.evaluate(expression, symbolTable)
                    symbolTable.setProperty(it.property)
                }
                is RsStatementExpression -> {
                    RsPassExpression.pass(it.expression, block.symbol, symbolTable)
                }
            }
        }
    }
}