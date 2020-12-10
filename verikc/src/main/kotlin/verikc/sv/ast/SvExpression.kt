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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.sv.build.SvBuildable
import verikc.sv.build.SvCompoundExpressionBuilder
import verikc.sv.build.SvSourceBuilder

sealed class SvExpression(
        override val line: Int
): Line, SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        SvCompoundExpressionBuilder.build(this, builder)
    }
}

data class SvExpressionControlBlock(
        override val line: Int,
        val type: SvControlBlockType,
        val args: List<SvExpression>,
        val blocks: List<SvBlock>
): SvExpression(line)

data class SvExpressionOperator(
        override val line: Int,
        val receiver: SvExpression?,
        val type: SvOperatorType,
        val args: List<SvExpression>
): SvExpression(line)

data class SvExpressionFunction(
        override val line: Int,
        val receiver: SvExpression?,
        val identifier: String,
        val args: List<SvExpression>
): SvExpression(line)

data class SvExpressionProperty(
        override val line: Int,
        val receiver: SvExpression?,
        val identifier: String
): SvExpression(line)

data class SvExpressionLiteral(
        override val line: Int,
        val string: String
): SvExpression(line)