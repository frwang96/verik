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

package verik.core.sv.ast

import verik.core.base.ast.Line
import verik.core.sv.build.SvBuildable
import verik.core.sv.build.SvSourceBuilder

sealed class SvStatement(
        override val line: Int
): Line, SvBuildable

data class SvStatementExpression(
        val expression: SvExpression
): SvStatement(expression.line) {

    override fun build(builder: SvSourceBuilder) {
        expression.build(builder)
    }

    companion object {

        fun wrapControlBlock(
                line: Int,
                type: SvControlBlockType,
                args: List<SvExpression>,
                blocks: List<SvBlock>
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionControlBlock(line, type, args, blocks))
        }

        fun wrapOperator(
                line: Int,
                receiver: SvExpression?,
                type: SvOperatorType,
                args: List<SvExpression>,
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionOperator(line, receiver, type, args))
        }

        fun wrapFunction(
                line: Int,
                receiver: SvExpression?,
                identifier: String,
                args: List<SvExpression>
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionFunction(line, receiver, identifier, args))
        }

        fun wrapProperty(
                line: Int,
                receiver: SvExpression?,
                identifier: String
        ): SvStatementExpression {
            return SvStatementExpression(SvExpressionProperty(line, receiver, identifier))
        }
    }
}