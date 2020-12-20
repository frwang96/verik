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

package verikc.kt.ast

import verikc.al.AlRule
import verikc.base.symbol.SymbolContext
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.parse.KtParserStatement

sealed class KtStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: AlRule, symbolContext: SymbolContext): KtStatement {
            return KtParserStatement.parse(statement, symbolContext)
        }
    }
}

data class KtStatementDeclaration(
    val primaryProperty: KtPrimaryProperty
): KtStatement(primaryProperty.line)

data class KtStatementExpression(
    val expression: KtExpression
): KtStatement(expression.line) {

    companion object {

        fun wrapFunction(
            line: Line,
            typeSymbol: Symbol?,
            identifier: String,
            receiver: KtExpression?,
            args: List<KtExpression>,
            functionSymbol: Symbol?
        ): KtStatementExpression {
            return KtStatementExpression(
                KtExpressionFunction(
                    line,
                    typeSymbol,
                    identifier,
                    receiver,
                    args,
                    functionSymbol
                )
            )
        }

        fun wrapProperty(
            line: Line,
            typeSymbol: Symbol?,
            identifier: String,
            receiver: KtExpression?,
            propertySymbol: Symbol?
        ): KtStatementExpression {
            return KtStatementExpression(KtExpressionProperty(line, typeSymbol, identifier, receiver, propertySymbol))
        }

        fun wrapLiteral(
            line: Line,
            typeSymbol: Symbol?,
            value: LiteralValue
        ): KtStatementExpression {
            return KtStatementExpression(KtExpressionLiteral(line, typeSymbol, value))
        }
    }
}
