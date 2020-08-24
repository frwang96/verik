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
import verik.core.al.AlRuleType
import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.lang.LangSymbol.TYPE_INT

sealed class KtStatement(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(statement: AlRule): KtStatement {
            val child = statement.firstAsRule()
            return when (child.type) {
                AlRuleType.DECLARATION -> {
                    // TODO support declaration statements
                    KtStatementExpression(child.line, KtExpressionLiteral(child.line, TYPE_INT, LiteralValue.fromIntImplicit(0)))
                }
                AlRuleType.LOOP_STATEMENT -> {
                    // TODO support loop statements
                    KtStatementExpression(child.line, KtExpressionLiteral(child.line, TYPE_INT, LiteralValue.fromIntImplicit(0)))
                }
                AlRuleType.EXPRESSION -> {
                    KtStatementExpression(statement.line, KtExpression(child))
                }
                else -> throw LineException("declaration or loop or expression expected", statement)
            }
        }
    }
}

data class KtStatementExpression(
        override val line: Int,
        val expression: KtExpression
): KtStatement(line)