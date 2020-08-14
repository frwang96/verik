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

package verik.core.svx

import verik.core.main.Line

sealed class SvxExpression(
        override val line: Int
): Line {

    fun build(): String {
        return SvxExpressionBuilder.build(this)
    }
}

data class SvxExpressionFunction(
        override val line: Int,
        val target: SvxExpression?,
        val identifier: String,
        val args: List<SvxExpression>
): SvxExpression(line)

data class SvxExpressionOperator(
        override val line: Int,
        val target: SvxExpression?,
        val identifier: SvxOperatorIdentifier,
        val args: List<SvxExpression>
): SvxExpression(line)

data class SvxExpressionProperty(
        override val line: Int,
        val target: SvxExpression?,
        val identifier: String
): SvxExpression(line)

data class SvxExpressionLiteral(
        override val line: Int,
        val string: String
): SvxExpression(line)