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

import io.verik.core.FileLine
import io.verik.core.kt.KtRule
import io.verik.core.sv.SvExpression
import io.verik.core.sv.SvStatement

sealed class VkExpression(
        open var dataType: VkDataType,
        open val fileLine: FileLine) {

    fun extractStatement(): SvStatement {
        return VkStatementExtractor.extractStatement(this)
    }

    fun extractExpression(): SvExpression {
        return VkExpressionExtractor.extractExpression(this)
    }

    companion object {

        operator fun invoke(expression: KtRule): VkExpression {
            return VkExpressionParser.parse(expression)
        }
    }
}

data class VkLambdaExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val statements: List<VkStatement>
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, statements: List<VkStatement>): this(VkUnitType, fileLine, statements)
}

data class VkCallableExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val target: VkExpression,
        val args: List<VkExpression>
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, target: VkExpression, args: List<VkExpression>):
            this(VkUnitType, fileLine, target, args)
}

data class VkOperatorExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val type: VkOperatorType,
        val args: List<VkExpression>
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, type: VkOperatorType, args: List<VkExpression>):
            this(VkUnitType, fileLine, type, args)
}

data class VkNavigationExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val target: VkExpression,
        val identifier: String
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, target: VkExpression, identifier: String):
            this(VkUnitType, fileLine, target, identifier)
}

data class VkIdentifierExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val identifier: String
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, identifier: String): this(VkUnitType, fileLine, identifier)
}

data class VkLiteralExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val value: String
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, value: String): this(VkUnitType, fileLine, value)
}

data class VkStringExpression(
        override var dataType: VkDataType,
        override val fileLine: FileLine,
        val segments: List<VkStringSegment>
): VkExpression(dataType, fileLine) {

    constructor(fileLine: FileLine, segments: List<VkStringSegment>): this(VkUnitType, fileLine, segments)
}
