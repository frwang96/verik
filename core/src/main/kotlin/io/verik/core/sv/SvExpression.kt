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

import io.verik.core.Line

sealed class SvExpression(
        override val line: Int
): Line {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvExpressionCallable(
        override val line: Int,
        val target: SvExpression,
        val args: List<SvExpression>
): SvExpression(line)

data class SvExpressionOperator(
        override val line: Int,
        val type: SvOperatorType,
        val args: List<SvExpression>
): SvExpression(line)

data class SvExpressionIdentifier(
        override val line: Int,
        val identifier: String
): SvExpression(line)

data class SvExpressionLiteral(
        override val line: Int,
        val value: String
): SvExpression(line)

data class SvExpressionString(
        override val line: Int,
        val string: String
): SvExpression(line)
