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
import verikc.base.SymbolIndexer
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.parse.KtParserStatement

sealed class KtStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: AlRule, indexer: SymbolIndexer): KtStatement {
            return KtParserStatement.parse(statement, indexer)
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
            type: Symbol?,
            identifier: String,
            receiver: KtExpression?,
            args: List<KtExpression>,
            function: Symbol?
        ): KtStatementExpression {
            return KtStatementExpression(KtExpressionFunction(line, type, identifier, receiver, args, function))
        }

        fun wrapProperty(
            line: Line,
            type: Symbol?,
            identifier: String,
            receiver: KtExpression?,
            property: Symbol?
        ): KtStatementExpression {
            return KtStatementExpression(KtExpressionProperty(line, type, identifier, receiver, property))
        }

        fun wrapLiteral(
            line: Line,
            type: Symbol?,
            value: LiteralValue
        ): KtStatementExpression {
            return KtStatementExpression(KtExpressionLiteral(line, type, value))
        }
    }
}
