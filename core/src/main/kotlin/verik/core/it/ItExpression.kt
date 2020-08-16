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

package verik.core.it

import verik.core.main.Line
import verik.core.main.symbol.Symbol
import verik.core.sv.SvExpression
import verik.core.vk.VkExpression

sealed class ItExpression(
        override val line: Int,
        open val typeInstance: ItTypeInstance
): Line {

    fun extract(): SvExpression {
        return ItExpressionExtractor.extract(this)
    }

    companion object {

        operator fun invoke(expression: VkExpression): ItExpression {
            return ItExpressionInstantiator.instantiate(expression)
        }
    }
}

data class ItExpressionFunction(
        override val line: Int,
        override val typeInstance: ItTypeInstance,
        val target: ItExpression?,
        val args: List<ItExpression>,
        val function: Symbol
): ItExpression(line, typeInstance)

data class ItExpressionOperator(
        override val line: Int,
        override val typeInstance: ItTypeInstance,
        val target: ItExpression?,
        val identifier: ItOperatorIdentifier,
        val args: List<ItExpression>
): ItExpression(line, typeInstance)

data class ItExpressionProperty(
        override val line: Int,
        override val typeInstance: ItTypeInstance,
        val target: ItExpression?,
        val property: Symbol
): ItExpression(line, typeInstance)

data class ItExpressionString(
        override val line: Int,
        override val typeInstance: ItTypeInstance,
        val segments: List<ItStringSegment>
): ItExpression(line, typeInstance)

data class ItExpressionLiteral(
        override val line: Int,
        override val typeInstance: ItTypeInstance,
        val value: String
): ItExpression(line, typeInstance)