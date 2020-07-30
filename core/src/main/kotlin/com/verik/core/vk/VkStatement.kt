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

package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.kt.KtRule
import com.verik.core.kt.KtRuleType
import com.verik.core.sv.SvStatement

data class VkStatement(val expression: VkExpression, val linePos: LinePos) {

    fun extract(): SvStatement {
        return expression.extractStatement()
    }

    companion object {

        operator fun invoke(statement: KtRule): VkStatement {
            val child = statement.firstAsRule()
            return when (child.type) {
                 KtRuleType.DECLARATION -> {
                     throw LinePosException("declaration statements not supported", statement.linePos)
                 }
                 KtRuleType.LOOP_STATEMENT -> {
                     throw LinePosException("loop statements not supported", statement.linePos)
                 }
                 KtRuleType.EXPRESSION -> {
                     VkStatement(VkExpression(child), statement.linePos)
                 }
                 else -> throw LinePosException("declaration loop or expression expected", statement.linePos)
             }
        }
    }
}