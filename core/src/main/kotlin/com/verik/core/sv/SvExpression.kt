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

package com.verik.core.sv

import com.verik.core.LinePos

sealed class SvExpression(open val linePos: LinePos) {

    fun build(): String {
        return SvExpressionBuilder.build(this)
    }
}

data class SvCallableExpression(
        override val linePos: LinePos,
        val target: SvExpression,
        val args: List<SvExpression>
): SvExpression(linePos)

data class SvOperatorExpression(
        override val linePos: LinePos,
        val type: SvOperatorType,
        val args: List<SvExpression>
): SvExpression(linePos)

data class SvIdentifierExpression(
        override val linePos: LinePos,
        val identifier: String
): SvExpression(linePos)

data class SvLiteralExpression(
        override val linePos: LinePos,
        val value: String
): SvExpression(linePos)

data class SvStringExpression(
        override val linePos: LinePos,
        val string: String
): SvExpression(linePos)
