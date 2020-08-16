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
        open val type: Symbol,
        open var typeReified: ItTypeReified?
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
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val function: Symbol,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItExpression(line, type, typeReified)

data class ItExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val identifier: ItOperatorIdentifier,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItExpression(line, type, typeReified)

data class ItExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val property: Symbol,
        val target: ItExpression?
): ItExpression(line, type, typeReified)

data class ItExpressionString(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val segments: List<ItStringSegment>
): ItExpression(line, type, typeReified)

data class ItExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val value: String
): ItExpression(line, type, typeReified)