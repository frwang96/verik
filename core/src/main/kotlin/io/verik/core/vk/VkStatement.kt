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

package io.verik.core.vk

import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.sv.SvStatement

data class VkStatement(
        override val line: Int,
        val expression: VkExpression
): Line {

    fun extract(): SvStatement {
        return expression.extractStatement()
    }

    companion object {

        operator fun invoke(statement: AlRule): VkStatement {
            val child = statement.firstAsRule()
            return when (child.type) {
                 AlRuleType.DECLARATION -> {
                     throw LineException("declaration statements not supported", statement)
                 }
                 AlRuleType.LOOP_STATEMENT -> {
                     throw LineException("loop statements not supported", statement)
                 }
                 AlRuleType.EXPRESSION -> {
                     VkStatement(statement.line, VkExpression(child))
                 }
                 else -> throw LineException("declaration loop or expression expected", statement)
             }
        }
    }
}