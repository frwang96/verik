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

package io.verik.core.sv

import io.verik.core.FileLine

sealed class SvExpression(open val fileLine: FileLine) {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvExpressionCallable(
        override val fileLine: FileLine,
        val target: SvExpression,
        val args: List<SvExpression>
): SvExpression(fileLine)

data class SvExpressionOperator(
        override val fileLine: FileLine,
        val type: SvOperatorType,
        val args: List<SvExpression>
): SvExpression(fileLine)

data class SvExpressionIdentifier(
        override val fileLine: FileLine,
        val identifier: String
): SvExpression(fileLine)

data class SvExpressionLiteral(
        override val fileLine: FileLine,
        val value: String
): SvExpression(fileLine)

data class SvExpressionString(
        override val fileLine: FileLine,
        val string: String
): SvExpression(fileLine)
