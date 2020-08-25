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

package verik.core.sv

import verik.core.base.Line
import verik.core.sv.build.SvBuildable
import verik.core.sv.build.SvSourceBuilder
import verik.core.sv.build.SvStatementBuilder

sealed class SvStatement(
        override val line: Int
): Line, SvBuildable

data class SvStatementControlBlock(
        override val line: Int,
        val type: SvControlBlockType,
        val args: List<SvExpression>,
        val blocks: List<SvBlock>
): SvStatement(line) {

    override fun build(builder: SvSourceBuilder) {
        SvStatementBuilder.build(this, builder)
    }
}

data class SvStatementExpression(
        val expression: SvExpression
): SvStatement(expression.line) {

    override fun build(builder: SvSourceBuilder) {
        builder.append(expression.build())
        builder.appendln(";")
    }

    companion object {

        fun wrapFunction(
                line: Int,
                target: SvExpression?,
                identifier: String,
                args: List<SvExpression>
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionFunction(line, target, identifier, args))
        }

        fun wrapOperator(
                line: Int,
                target: SvExpression?,
                type: SvOperatorType,
                args: List<SvExpression>,
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionOperator(line, target, type, args))
        }

        fun wrapProperty(
                line: Int,
                target: SvExpression?,
                identifier: String
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionProperty(line, target, identifier))
        }
    }
}