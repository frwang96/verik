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
import verik.core.base.LiteralValue
import verik.core.base.SymbolIndexer
import verik.core.kt.*
import verik.core.lang.LangSymbol

object KtStatementParser {

    fun parse(statement: AlRule, indexer: SymbolIndexer): KtStatement {
        val child = statement.firstAsRule()
        return when (child.type) {
            AlRuleType.DECLARATION -> parseDeclaration(child, indexer)
            AlRuleType.LOOP_STATEMENT -> parseLoopStatement(child)
            AlRuleType.EXPRESSION -> KtStatementExpression(statement.line, KtExpression(child, indexer))
            else -> throw LineException("declaration or loop or expression expected", statement)
        }
    }

    private fun parseDeclaration(declaration: AlRule, indexer: SymbolIndexer): KtStatementDeclaration {
        val primaryProperty = KtDeclaration(declaration, indexer)
        if (primaryProperty !is KtDeclarationPrimaryProperty) {
            throw LineException("illegal declaration", primaryProperty)
        }
        return KtStatementDeclaration(
                primaryProperty.line,
                primaryProperty
        )
    }

    private fun parseLoopStatement(loopStatement: AlRule): KtStatementExpression {
        return KtStatementExpression(
                loopStatement.line,
                KtExpressionLiteral(loopStatement.line, LangSymbol.TYPE_INT, LiteralValue.fromIntImplicit(0))
        )
    }
}