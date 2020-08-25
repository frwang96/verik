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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.base.Line
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.base.SymbolIndexer
import verik.core.kt.parse.KtStatementParser

sealed class KtStatement(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(statement: AlRule, indexer: SymbolIndexer): KtStatement {
            return KtStatementParser.parse(statement, indexer)
        }
    }
}

data class KtStatementDeclaration(
        override val line: Int,
        val primaryProperty: KtDeclarationPrimaryProperty
): KtStatement(line)

data class KtStatementExpression(
        override val line: Int,
        val expression: KtExpression
): KtStatement(line) {

    companion object {

        fun wrapProperty(
                line: Int,
                type: Symbol?,
                identifier: String,
                target: KtExpression?,
                property: Symbol?
        ): KtStatementExpression {
            return KtStatementExpression(line, KtExpressionProperty(line, type, identifier, target, property))
        }

        fun wrapLiteral(
                line: Int,
                type: Symbol?,
                value: LiteralValue
        ): KtStatementExpression {
            return KtStatementExpression(line, KtExpressionLiteral(line, type, value))
        }
    }
}