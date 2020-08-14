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

package verik.core.vk

import verik.core.main.Line
import verik.core.al.AlRule
import verik.core.sv.SvExpression
import verik.core.sv.SvStatement

sealed class VkExpression(
        override val line: Int,
        open var instanceType: VkInstanceType
): Line {

    fun extractStatement(): SvStatement {
        return VkStatementExtractor.extractStatement(this)
    }

    fun extractExpression(): SvExpression {
        return VkExpressionExtractor.extractExpression(this)
    }

    companion object {

        operator fun invoke(expression: AlRule): VkExpression {
            return VkExpressionParser.parse(expression)
        }
    }
}

data class VkExpressionLambda(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val statements: List<VkStatement>
): VkExpression(line, instanceType) {

    constructor(line: Int, statements: List<VkStatement>): this(line, VkUnitType, statements)
}

data class VkExpressionCallable(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val target: VkExpression,
        val args: List<VkExpression>
): VkExpression(line, instanceType) {

    constructor(line: Int, target: VkExpression, args: List<VkExpression>):
            this(line, VkUnitType, target, args)
}

data class VkExpressionOperator(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val type: VkOperatorType,
        val args: List<VkExpression>
): VkExpression(line, instanceType) {

    constructor(line: Int, type: VkOperatorType, args: List<VkExpression>):
            this(line, VkUnitType, type, args)
}

data class VkExpressionNavigation(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val target: VkExpression,
        val identifier: String
): VkExpression(line, instanceType) {

    constructor(line: Int, target: VkExpression, identifier: String):
            this(line, VkUnitType, target, identifier)
}

data class VkExpressionIdentifier(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val identifier: String
): VkExpression(line, instanceType) {

    constructor(line: Int, identifier: String): this(line, VkUnitType, identifier)
}

data class VkExpressionLiteral(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val value: String
): VkExpression(line, instanceType) {

    constructor(line: Int, value: String): this(line, VkUnitType, value)
}

data class VkExpressionString(
        override val line: Int,
        override var instanceType: VkInstanceType,
        val segments: List<VkStringSegment>
): VkExpression(line, instanceType) {

    constructor(line: Int, segments: List<VkStringSegment>): this(line, VkUnitType, segments)
}
