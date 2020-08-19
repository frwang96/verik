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

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.al.AlRule
import verik.core.al.AlRuleType

data class KtStatement(
        override val line: Int,
        val expression: KtExpression
): Line {

    companion object {

        operator fun invoke(statement: AlRule): KtStatement {
            val child = statement.firstAsRule()
            return when (child.type) {
                AlRuleType.DECLARATION -> {
                    throw LineException("declarations not supported here", statement)
                }
                AlRuleType.LOOP_STATEMENT -> {
                    throw LineException("loop statements not supported", statement)
                }
                AlRuleType.EXPRESSION -> {
                    KtStatement(statement.line, KtExpression(child))
                }
                else -> throw LineException("declaration or loop or expression expected", statement)
            }
        }
    }
}